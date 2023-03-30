package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.User;
import com.interswitchgroup.tx_user_portal.models.request.NewOrganizationRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.NewRoleRequestModel;
import com.interswitchgroup.tx_user_portal.services.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @Autowired
    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    @PutMapping("/update-permission/{user_id}/{permission}")
    public ResponseEntity<String> updatePermission(@PathVariable long user_id, @PathVariable String permission){
        return new ResponseEntity<>("Permission updated successfully", HttpStatus.OK);
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<Page<Request>> getPendingRequests(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<Request> allRequests = superAdminService.getAllPendingRequests(pageNumber, pageSize);
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<User> allUsers = superAdminService.getAllUsers(pageNumber, pageSize);
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @PostMapping("/organization/add")
    public ResponseEntity<String> addOrganization(@RequestBody NewOrganizationRequestModel newOrganizationRequestModel){
        superAdminService.addNewOrganization(newOrganizationRequestModel);
        return new ResponseEntity<>("Organization added successfully", HttpStatus.OK);
    }

    @PostMapping("/roles/add")
    public ResponseEntity<String> addRole(@RequestBody NewRoleRequestModel roleRequestModel){
        superAdminService.addNewRole(roleRequestModel);
        return new ResponseEntity<>("Role added successfully", HttpStatus.OK);
    }

    @PostMapping("/users/add")
    public ResponseEntity<String> addUser(){
        return new ResponseEntity<>("User added successfully", HttpStatus.OK);
    }
}
