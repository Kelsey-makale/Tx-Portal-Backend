package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.User;
import com.interswitchgroup.tx_user_portal.models.*;
import com.interswitchgroup.tx_user_portal.services.UserFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/user-request")
public class UserFormController {
    private final UserFormService userFormService;
    @Autowired
    public UserFormController(UserFormService formService){
        this.userFormService = formService;
    }

    @GetMapping("/hello")
    public ResponseEntity<UserResponseModel> helloWorld(){
        UserResponseModel responseModel = new UserResponseModel("Success", "Hello World", Optional.empty());
        return ResponseEntity.ok(responseModel);
    }

    @PostMapping("/request-role")
    public ResponseEntity<String> requestRole(@RequestBody UserRoleRequestModel userRoleRequestModel){
        return new ResponseEntity<>("Role request submitted successfully", HttpStatus.CREATED);
    }

}
