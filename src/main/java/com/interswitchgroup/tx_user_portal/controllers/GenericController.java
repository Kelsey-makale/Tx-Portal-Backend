package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.Organization;
import com.interswitchgroup.tx_user_portal.entities.Role;
import com.interswitchgroup.tx_user_portal.models.*;
import com.interswitchgroup.tx_user_portal.services.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1")
public class GenericController {
    private final GenericService genericService;
    @Autowired
    public GenericController(GenericService genericService) {
        this.genericService = genericService;
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody UserSignInRequestModel userSignInRequestModel){
        String resp = genericService.signInUser(userSignInRequestModel);
        return new ResponseEntity<>("User Signed In Successfully", HttpStatus.CREATED);
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpRequestModel userSignUpRequestModel){
        String resp = genericService.createUser(userSignUpRequestModel);
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

    @GetMapping("/organizations-roles")
    public ResponseEntity<Map<String, Object>> getRequestData(){
        return new ResponseEntity<>(genericService.getOrganizationAndRoleData(), HttpStatus.OK);
    }

    @PostMapping("/request")
    public ResponseEntity<String> makePermissionRequest(@RequestBody UserRoleRequestModel requestModel){
        genericService.makeRequest(requestModel);
        return new ResponseEntity<>("Request logged successfully", HttpStatus.OK);
    }

}
