package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.User;
import com.interswitchgroup.tx_user_portal.entities.UserDetails;
import com.interswitchgroup.tx_user_portal.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @PutMapping("/update-request/{request_id}/{request_status}")
    public ResponseEntity<String> updateRequest(@PathVariable long request_id, @PathVariable String request_status){
        return new ResponseEntity<>("Request updated successfully", HttpStatus.OK);
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<Page<Request>> getPendingRequests(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<Request> allRequests = adminService.getAllPendingRequests(pageNumber, pageSize);
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getSpecificUser(@RequestParam long userId){
        return new ResponseEntity<>(adminService.getSpecificUser(userId), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<User> allUsers = adminService.getAllUsers(pageNumber, pageSize);
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

}
