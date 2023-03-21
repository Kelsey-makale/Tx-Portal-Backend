package com.interswitchgroup.tx_user_portal.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/admin")
public class AdminController {

    @PutMapping("/update-request/{request_id}/{request_status}")
    public ResponseEntity<String> updateRequest(@PathVariable long request_id, @PathVariable String request_status){
        return new ResponseEntity<>("Request updated successfully", HttpStatus.OK);
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
