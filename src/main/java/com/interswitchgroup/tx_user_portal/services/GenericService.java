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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private  final AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    public GenericService(UserRepository userRepository, UserDetailsRepository userDetailsRepository, OrganizationRepository organizationRepository, RoleRepository roleRepository, UserVerificationRepository userVerificationRepository, RequestRepository requestRepository, EmailService emailService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.requestRepository = requestRepository;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    public UserResponseModel createUser(UserSignUpRequestModel userSignUpRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();
        try {
            Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignUpRequestModel.getEmail_address());
            Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(userSignUpRequestModel.getOrganization_id());

            if (userOptional.isPresent()) {
                System.out.println("USER ALREADY EXISTS");
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
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userSignInRequestModel.getEmail_address(),
                            userSignInRequestModel.getPassword()
                    )
            );

            Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignInRequestModel.getEmail_address());
            User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userSignInRequestModel.getEmail_address()));

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

        } catch (AuthenticationException e) {
            response.put("error", e.getMessage());
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
            userVerification.setExpires_at(LocalDateTime.now().plusMinutes(15));
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
                    LocalDateTime.now().plusMinutes(15)// Set to expire in 15 min
            );
            //save OTP (with an expiry)
            userVerificationRepository.save(userVerification);
        }


        //4. Send user email containing the OTP
        //emailService.sendMail(user.getEmailAddress(), "Verify Account", "Your OTP is" + generatedOTP);
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
                    responseModel = new UserResponseModel(
                            HttpStatus.OK.value(),
                            "User successfully verified."
                    );
                }
                else{
                    response.put("error", "Invalid OTP");
                    responseModel = new UserResponseModel(
                            HttpStatus.EXPECTATION_FAILED.value(),
                            "Failed to verify user",
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
        //todo: figure out how to handle duplicates. A user making the same request twice.
        //todo: handle when the user is blocked (POSTPONE)
        //todo: send email to info_sec and bank admin

        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();

        try{
            Optional<User> userOptional = userRepository.findUserByUserId(requestModel.getUserId());
            User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found: " + requestModel.getUserId()));

            Request newRequest = new Request();
            newRequest.setUser(user);
            newRequest.setOrganizationId(user.getUserDetails().getOrganization().getOrganization_id());
            newRequest.setRequestStatus(RequestStatus.PENDING);
            newRequest.setDateCreated(LocalDateTime.now());
            newRequest.setDateUpdated(LocalDateTime.now());

            List<Role> fetchedRoles = roleRepository.findAllById(requestModel.getRoleIds());

            Set<Role> set = new HashSet<>(fetchedRoles);
            newRequest.setRoles(set);
            requestRepository.save(newRequest);

            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "OTP sent successfully."
            );


        }catch(IllegalArgumentException e){
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to resend OTP",
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
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.searchByMultipleColumns(searchTerm, pageable);
    }
}
