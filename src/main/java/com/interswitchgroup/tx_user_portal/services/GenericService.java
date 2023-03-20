package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.*;
import com.interswitchgroup.tx_user_portal.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GenericService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final RequestRepository requestRepository;
    private final EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public GenericService(UserRepository userRepository, OrganizationRepository organizationRepository, RoleRepository roleRepository, UserVerificationRepository userVerificationRepository, RequestRepository requestRepository, EmailService emailService) {
        this.userRepository = userRepository;
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
        User created_user;

        if(userOptional.isPresent()){
            System.out.println("USER ALREADY EXISTS");
            return userOptional.map(user -> String.valueOf(user.getUser_id())).orElse("Exists");
        } else{
            String encPass = passwordEncoder.encode(userSignUpRequestModel.getPassword());
            User newUser = new User(
                    userSignUpRequestModel.getFirst_name(),
                    userSignUpRequestModel.getSecond_name(),
                    userSignUpRequestModel.getEmail_address(),
                    userSignUpRequestModel.getPhone_number(),
                    encPass,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            created_user =  userRepository.save(newUser);
            System.out.println("CREATED USER'S ID::" + created_user.getUser_id());

            //3. Generate OTP
            String my_otp = generateOTP(created_user.getUser_id());

            //4. Send user email containing the OTP
           // emailService.sendMail(userSignUpRequestModel.getEmail_address(), "Verify Account", "Your OTP is" + my_otp);

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
        Optional<User> userOptional = userRepository.findUserByUserId(requestModel.getUserId());
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found: " + requestModel.getUserId()));

        Request newRequest = new Request(
                user,
                requestModel.getRoleIds(),
                "PENDING",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        requestRepository.save(newRequest);
    }

}
