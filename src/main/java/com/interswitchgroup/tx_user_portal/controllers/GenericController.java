package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.User;
import com.interswitchgroup.tx_user_portal.models.request.*;
import com.interswitchgroup.tx_user_portal.models.response.OrganizationRightsResponseModel;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.services.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/public")
public class GenericController {

    private final GenericService genericService;


    @Autowired
    public GenericController(GenericService genericService) {
        this.genericService = genericService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        try{
            System.out.println("HELLO WORLD");
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

    @PostMapping("/signIn")
    public ResponseEntity<UserResponseModel> signIn(@RequestBody UserSignInRequestModel userSignInRequestModel){
        UserResponseModel resp = genericService.signInUser(userSignInRequestModel);
        return new ResponseEntity<>(resp, HttpStatus.valueOf(resp.getStatus()));
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserResponseModel> signUp(@RequestBody UserSignUpRequestModel userSignUpRequestModel){
        UserResponseModel resp = genericService.createUser(userSignUpRequestModel);
        return new ResponseEntity<>(resp, HttpStatusCode.valueOf(resp.getStatus()));
    }

    @PostMapping("/verify")
    public ResponseEntity<UserResponseModel> verifyEmail(@RequestBody UserVerifyRequestModel userVerifyRequestModel){
        UserResponseModel responseModel = genericService.verifyEmail(userVerifyRequestModel);
        return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getStatus()));
    }


    @PostMapping("/resendOtp")
    public ResponseEntity<UserResponseModel> resendOTP(@RequestBody ResendOTPRequestModel resendOTPRequestModel){
        System.out.println("DATA::"+resendOTPRequestModel);
        UserResponseModel responseModel = genericService.resendVerificationCode(resendOTPRequestModel);
        return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getStatus()));
    }

    @GetMapping("/organizations")
    public ResponseEntity<UserResponseModel> getOrganizationData(){
        return new ResponseEntity<>(genericService.getOrganizationData(), HttpStatus.OK);
    }


}
