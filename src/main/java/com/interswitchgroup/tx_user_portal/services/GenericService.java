package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.*;
import com.interswitchgroup.tx_user_portal.repositories.*;
import com.interswitchgroup.tx_user_portal.utils.Enums.RequestStatus;
import com.interswitchgroup.tx_user_portal.utils.Enums.UserPermission;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public GenericService(UserRepository userRepository, UserDetailsRepository userDetailsRepository,  OrganizationRepository organizationRepository, RoleRepository roleRepository, UserVerificationRepository userVerificationRepository, RequestRepository requestRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.requestRepository = requestRepository;
        this.emailService = emailService;
    }


    public String createUser(UserSignUpRequestModel userSignUpRequestModel){
        //1. Add user to database and return userID

        //check if they exist on the db first
        Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignUpRequestModel.getEmail_address());
        Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(userSignUpRequestModel.getOrganization_id());

        if(userOptional.isPresent()){
            System.out.println("USER ALREADY EXISTS");
            throw new IllegalArgumentException("A user with this email already exists: " + userSignUpRequestModel.getEmail_address());
        }
        else if(organizationOptional.isEmpty()){
            throw new IllegalArgumentException("Organization not found: " + userSignUpRequestModel.getOrganization_id());
        }
        else{
            String encPass = passwordEncoder.encode(userSignUpRequestModel.getPassword());
            List<Integer> my_roles = new ArrayList<>();
            User newUser = new User(
                    userSignUpRequestModel.getEmail_address(),
                    encPass,
                    my_roles,
                    UserPermission.BANK_USER,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );


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
            User created_user =  userRepository.save(newUser);

            //3. Generate OTP
            String my_otp = generateOTP(created_user.getUser_id());

            /*
            4. Send user email containing the OTP
            emailService.sendMail(userSignUpRequestModel.getEmail_address(), "Verify Account", "Your OTP is" + my_otp);
             */

            return created_user.getUser_id().toString();

        }
    }

    public String signInUser(UserSignInRequestModel userSignInRequestModel){
        //1. verify if user exists
        Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignInRequestModel.getEmail_address());
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userSignInRequestModel.getEmail_address()));
        if (passwordEncoder.matches(userSignInRequestModel.getPassword(), user.getPassword())) {
            //2. send them an otp
            String my_otp = generateOTP(user.getUser_id());
            //emailService.sendMail(userSignInRequestModel.getEmail_address(), "Verify Account", "Your OTP is " + my_otp);
            return user.getEmailAddress();
        } else {
            throw new IllegalArgumentException("Invalid credentials for user with email: " + userSignInRequestModel.getEmail_address());
        }
    }

    public String generateOTP(long userId){
        //1. generate 5 digit OTP
        Random random = new Random();
        String generatedOTP = String.valueOf(random.nextInt(90000) + 10000);

        //2. save OTP (with an expiry)
        UserVerification userVerification = new UserVerification(
                generatedOTP,
                String.valueOf(userId),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)// Set to expire in 15 min
        );
        userVerificationRepository.save(userVerification);
        System.out.println("YOUR OTP IS :: "+ generatedOTP);
        return generatedOTP;
    }

    public void verifyEmail(UserVerifyRequestModel userVerifyRequestModel){}

    public void resendVerificationCode(ResendOTPRequestModel resendOTPRequestModel){}

    public Map<String, Object> getOrganizationAndRoleData(){
        List<Organization> organizationList = organizationRepository.findAll();
        List<Role> roleList = roleRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("organizations", organizationList);
        response.put("roles", roleList);

        return response;
    }

    public void makeRequest(UserRoleRequestModel requestModel){
        //todo: figure out how to handle duplicates.
        Optional<User> userOptional = userRepository.findUserByUserId(requestModel.getUserId());
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found: " + requestModel.getUserId()));

        Request newRequest = new Request(
                user,
                requestModel.getRoleIds(),
                RequestStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        requestRepository.save(newRequest);
    }

}
