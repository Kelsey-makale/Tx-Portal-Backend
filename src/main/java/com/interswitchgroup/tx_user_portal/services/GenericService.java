package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.*;
import com.interswitchgroup.tx_user_portal.models.request.*;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.repositories.*;
import com.interswitchgroup.tx_user_portal.security.JwtUtil;
import com.interswitchgroup.tx_user_portal.utils.Enums.RequestStatus;
import com.interswitchgroup.tx_user_portal.utils.Enums.UserPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GenericService {
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final RequestRepository requestRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogsRepository auditLogsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    public GenericService(UserRepository userRepository, UserDetailsRepository userDetailsRepository, OrganizationRepository organizationRepository, RoleRepository roleRepository, UserVerificationRepository userVerificationRepository, RequestRepository requestRepository, EmailService emailService, AuthenticationManager authenticationManager, AuditLogsRepository auditLogsRepository) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.requestRepository = requestRepository;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.auditLogsRepository = auditLogsRepository;
    }

    public UserResponseModel createUser(UserSignUpRequestModel userSignUpRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();
        try {
            Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignUpRequestModel.getEmail_address());
            Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(userSignUpRequestModel.getOrganization_id());

            if (userOptional.isPresent()) {
                throw new IllegalArgumentException("A user with this email already exists: " + userSignUpRequestModel.getEmail_address());
            }
            if (organizationOptional.isEmpty()) {
                throw new IllegalArgumentException("Organization not found: " + userSignUpRequestModel.getOrganization_id());
            }
            else {
                String encPass = passwordEncoder.encode(userSignUpRequestModel.getPassword());
                Set<Role> my_roles = new HashSet<>();
                User newUser = new User();

                newUser.setEmailAddress(userSignUpRequestModel.getEmail_address());
                newUser.setPassword(encPass);
                newUser.setRoles(my_roles);
                newUser.setPermission( UserPermission.BANK_USER);
                newUser.setDateCreated(LocalDateTime.now());
                newUser.setDateUpdated(LocalDateTime.now());

                //2.a Add user details to db
                UserDetails newUserDetails = new UserDetails(
                        userSignUpRequestModel.getFirst_name(),
                        userSignUpRequestModel.getSecond_name(),
                        userSignUpRequestModel.getPhone_number(),
                        userSignUpRequestModel.getDesignation(),
                        userSignUpRequestModel.getDepartment(),
                        userSignUpRequestModel.getOffice_number(),
                        organizationOptional.get(),
                        LocalDateTime.now(),
                        LocalDateTime.now());

                newUser.setUserDetails(newUserDetails);
                User created_user = userRepository.save(newUser);

                //3. Generate OTP
                generateOTP(created_user);

            }

            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "User registered Successfully"
            );
        }
        catch(IllegalArgumentException e){
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to register user",
                    Optional.of(responseBody)
            );
        }
        return responseModel;
    }

    public UserResponseModel signInUser(UserSignInRequestModel userSignInRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> response = new HashMap<>();
        try {

            Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignInRequestModel.getEmail_address());
            User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userSignInRequestModel.getEmail_address()));

            //check role.
            if(user.getPermission().equals(UserPermission.BANK_ADMIN)){
                if(!user.getUserDetails().isVerified()){
                    throw new IllegalArgumentException("Please reset your password to proceed.");
                }
            } else if (user.getPermission().equals(UserPermission.BANK_USER)) {
                if(!user.getUserDetails().isVerified()){
                    System.out.println("AM I VERIFIED:: "+user.getUserDetails().isVerified());
                    throw new IllegalArgumentException("Please verify your email to proceed.");
                }
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userSignInRequestModel.getEmail_address(),
                            userSignInRequestModel.getPassword()
                    )
            );


            //create jwt
            CreatedUser createdUser = new CreatedUser(
                    user.getEmailAddress(),
                    user.getUserDetails().getOrganization().getOrganization_id());

            String jwt = JwtUtil.createJwt(createdUser);

            //return success message with jwt
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", jwt);
            responseData.put("user", user);

            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "User successfully signed in",
                    Optional.of(responseData)
            );

        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage(),
                    Optional.of(response)
            );
        }catch (AuthenticationException e) {
            response.put("Error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage(),
                    Optional.of(response)
            );
        }


        return responseModel;
    }

    public void generateOTP(User user){
        Optional<UserVerification> userVerificationOptional = userVerificationRepository.findUserVerificationByUserUserId(user.getUser_id());

        //1. generate 5 digit OTP
        Random random = new Random();
        String generatedOTP = String.valueOf(random.nextInt(90000) + 10000);
        UserVerification userVerification;

        if(userVerificationOptional.isPresent()){
            //perform update operation
            userVerification = userVerificationOptional.get();
            userVerification.setCreated_at(LocalDateTime.now());
            userVerification.setExpires_at(LocalDateTime.now().plusMinutes(2));
            userVerification.setOtp_code(generatedOTP);

            //save OTP (with an expiry)
            userVerificationRepository.save(userVerification);
        }
        else{
            //perform create operation
            userVerification = new UserVerification(
                    generatedOTP,
                    user,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(2)// Set to expire in 15 min
            );
            //save OTP (with an expiry)
            userVerificationRepository.save(userVerification);
        }


        //4. Send user email containing the OTP
        emailService.sendMail(user.getEmailAddress(), "Verify Account", "Your OTP is " + generatedOTP);
        System.out.println("YOUR OTP IS :: "+ generatedOTP);
    }

    public UserResponseModel verifyEmail(UserVerifyRequestModel userVerifyRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> response = new HashMap<>();

        try{
            Optional<User> userOptional = userRepository.findUserByEmailAddress(userVerifyRequestModel.getEmail_address());
            User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userVerifyRequestModel.getEmail_address()));

            Optional<UserVerification> userVerificationOptional = userVerificationRepository.findUserVerificationByUserUserId(user.getUser_id());

            if(userVerificationOptional.isPresent()){
                UserVerification userVerificationObj = userVerificationOptional.get();

                if(userVerificationObj.getOtp_code().equals(userVerifyRequestModel.getVerification_code())){

                    //CHECK IF THE TOKEN HAS EXPIRED
                    LocalDateTime timeNow = LocalDateTime.now();
                    if(timeNow.isAfter(userVerificationObj.getExpires_at())){
                        user.getUserDetails().setVerified(true);
                        userRepository.save(user);
                        responseModel = new UserResponseModel(
                                HttpStatus.EXPECTATION_FAILED.value(),
                                "The OTP provided has expired. Please click on resend code to generate a new OTP."
                        );
                    }
                    else {
                        user.getUserDetails().setVerified(true);
                        userRepository.save(user);
                        responseModel = new UserResponseModel(
                                HttpStatus.OK.value(),
                                "User successfully verified."
                        );
                    }
                }
                else{
                    response.put("error", "Invalid OTP");
                    responseModel = new UserResponseModel(
                            HttpStatus.EXPECTATION_FAILED.value(),
                            "The OTP provided is invalid.",
                            Optional.of(response)
                    );
                }
            }
            else{
                //return record does not exist
                throw new IllegalArgumentException("Record does not exist. User needs to sign up first.");
            }
        }
        catch(IllegalArgumentException e){
            response.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to register user",
                    Optional.of(response)
            );
        }

        return responseModel;

    }

    public UserResponseModel resendVerificationCode(ResendOTPRequestModel resendOTPRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();

        try{
            Optional<User> userOptional = userRepository.findUserByEmailAddress(resendOTPRequestModel.getEmail_address());
            User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + resendOTPRequestModel.getEmail_address()));

            //generate OTP & send it via email
            generateOTP(user);

            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "OTP sent successfully."
            );


        }catch (IllegalArgumentException e){
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to resend OTP",
                    Optional.of(responseBody)
            );
        }
        return responseModel;
    }

    public UserResponseModel getOrganizationData(){
        UserResponseModel responseModel;
        Map<String, Object> response = new HashMap<>();

        try{
            List<Organization> organizationList = organizationRepository.findAll();
            response.put("organizations", organizationList);
            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "Success",
                    Optional.of(response)
            );

        }catch(Exception e){
            response.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to fetch organizations",
                    Optional.of(response)
            );
        }


        return responseModel;
    }

    public UserResponseModel getRoleData(){
        UserResponseModel responseModel;
        Map<String, Object> response = new HashMap<>();

        try{
            List<Role> roleList = roleRepository.findAll();
            response.put("roles", roleList);

            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "Success",
                    Optional.of(response)
            );

        }
        catch(Exception e){
            response.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to fetch organizations",
                    Optional.of(response)
            );
        }


        return responseModel;
    }

    public UserResponseModel makeRequest(UserRoleRequestModel requestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();

        try{
            Optional<User> userOptional = userRepository.findUserByUserId(requestModel.getUserId());
            User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found: " + requestModel.getUserId()));

            //todo:preventing duplicates: By blocking user from making more than one request if one is pending
            List<Request> allMyRequests =  requestRepository.getAllMyPendingRequests(user.getUser_id());

            if(allMyRequests.size() > 0){
                responseModel = new UserResponseModel(
                        HttpStatus.EXPECTATION_FAILED.value(),
                        "You have pending requests that need to be approved first."
                );
                throw new IllegalArgumentException("You have pending requests that need to be approved first.");
            }else{
                //get all roles in the new request
                List<Role> fetchedRoles = roleRepository.findAllById(requestModel.getRoleIds());
                Set<Role> set = new HashSet<>(fetchedRoles);


                Request newRequest = new Request();
                newRequest.setUser(user);
                newRequest.setOrganizationId(user.getUserDetails().getOrganization().getOrganization_id());
                newRequest.setRequestStatus(RequestStatus.PENDING);
                newRequest.setDateCreated(LocalDateTime.now());
                newRequest.setDateUpdated(LocalDateTime.now());

                newRequest.setRoles(set);
                requestRepository.save(newRequest);

                responseModel = new UserResponseModel(
                        HttpStatus.OK.value(),
                        "Request logged successfully."
                );

                //send email to bank user and cc bank admin(s).
                String superAdminEmail = userRepository.getSuperAdmin();
                List<String> bankAdminsEmails = userRepository.getMyAdmins(user.getUserDetails().getOrganization().getOrganization_id());

                emailService.sendMailWithCC(superAdminEmail,
                        "Request pending Approval",
                        "A new request is pending your approval. Please review and take necessary action.",
                        bankAdminsEmails.toArray(new String[0]));

            }
        }catch(IllegalArgumentException e){
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage(),
                    Optional.of(responseBody)
            );
        }
        return responseModel;
    }

    public UserResponseModel editRequest(UserEditRoleRequestModel requestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();

        try{
            Optional<Request> requestOptional = requestRepository.findRequestByRequestId(requestModel.getRequestId());
            Request request = requestOptional.orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestModel.getRequestId()));

            //check if request is still pending
            if (request.getRequestStatus().name().equals("PENDING")){
                List<Role> fetchedRoles = roleRepository.findAllById(requestModel.getRoleIds());

                Set<Role> set = new HashSet<>(fetchedRoles);
                request.setRoles(set);
                requestRepository.save(request);

                responseModel = new UserResponseModel(
                        HttpStatus.OK.value(),
                        "Request logged successfully."
                );
            }
            else{
                responseModel = new UserResponseModel(
                        HttpStatus.EXPECTATION_FAILED.value(),
                        "The request cannot be modified at this time since its status is " + request.getRequestStatus().name()
                );
            }

        }catch(IllegalArgumentException e){
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to edit request",
                    Optional.of(responseBody)
            );
        }
        return responseModel;
    }

    public Page<Request> getMyRequests(int pageNumber, int pageSize) {
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            long user_id = currentAdmin.getUser_id();
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return requestRepository.findAll(areMine(user_id),pageable);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public UserResponseModel cancelRequest(CancelRequestModel cancelRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();

        try{
            Optional<Request> requestOptional = requestRepository.findRequestByRequestId(cancelRequestModel.getRequest_id());
            if(requestOptional.isEmpty()){
                throw new IllegalArgumentException("Request not found: " + cancelRequestModel.getRequest_id());
            }
            else {
                Request request = requestOptional.get();
                if (request.getRequestStatus().name().equals("PENDING")){
                    Request foundRequest = requestOptional.get();
                    foundRequest.setRequestStatus(RequestStatus.CANCELLED);
                    requestRepository.save(foundRequest);
                    responseModel = new UserResponseModel(
                            HttpStatus.OK.value(),
                            "Request cancelled successfully."
                    );
                }else{
                    responseModel = new UserResponseModel(
                            HttpStatus.EXPECTATION_FAILED.value(),
                            "The request cannot be modified at this time since its status is " + request.getRequestStatus().name()
                    );
                    }
            }
        }catch(Exception e){
            e.printStackTrace();
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to edit request",
                    Optional.of(responseBody)
            );
        }

        return responseModel;
    }

    /**
     * This method returns a JPA Specification that is used to eagerly fetch the requests marked as PENDING
     **/
    private static Specification<Request> areMine(long userId) {
        return (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get("user").get("userId"), userId)
            );
        };
    }

    public Page<User> usersFuzzySearch(String searchTerm, int pageNumber, int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return userRepository.searchAllUsers(searchTerm, pageable);
            }
            else if (user_permission.equals("BANK_ADMIN")) {
                long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return userRepository.searchMyOrgUsers(org_id, searchTerm, pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
