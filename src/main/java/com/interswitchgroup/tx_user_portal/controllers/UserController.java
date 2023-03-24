package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.models.request.UserRoleRequestModel;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.services.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    private final GenericService genericService;

    @Autowired
    public UserController(GenericService genericService) {
        this.genericService = genericService;
    }

    @GetMapping("/roles")
    public ResponseEntity<UserResponseModel> getRoleData(){
        return new ResponseEntity<>(genericService.getRoleData(), HttpStatus.OK);
    }


    @PostMapping("/request")
    public ResponseEntity<String> makePermissionRequest(@RequestBody UserRoleRequestModel requestModel){
        genericService.makeRequest(requestModel);
        return new ResponseEntity<>("Request logged successfully", HttpStatus.OK);
    }
}
