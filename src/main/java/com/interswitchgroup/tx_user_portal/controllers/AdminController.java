package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.models.ResendOTPRequestModel;
import com.interswitchgroup.tx_user_portal.models.UserSignUpRequestModel;
import com.interswitchgroup.tx_user_portal.models.UserVerifyRequestModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/admin")
public class AdminController {

    @PostMapping("/signIn")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpRequestModel userSignUpRequestModel){
        return new ResponseEntity<>("User Created Successfully", HttpStatus.CREATED);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOTP(@RequestBody ResendOTPRequestModel resendOTPRequestModel){
        return new ResponseEntity<>("OTP sent Successfully", HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody UserVerifyRequestModel userVerifyRequestModel){
        return new ResponseEntity<>("Email Verified Successfully", HttpStatus.OK);
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<String> getPendingRequests(){
        return new ResponseEntity<>("Fetched Successfully", HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<String> getAllUsers(){
        return new ResponseEntity<>("Fetched Successfully", HttpStatus.OK);
    }

}
