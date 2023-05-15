package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.User;
import com.interswitchgroup.tx_user_portal.entities.UserDetails;
import com.interswitchgroup.tx_user_portal.models.request.CommentRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.UpdateRequestStatusRequestModel;
import com.interswitchgroup.tx_user_portal.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/users/update-status/{user_id}/{account_status}")
    public ResponseEntity<String> updateUserAccount(@PathVariable long user_id, @PathVariable String account_status){
        adminService.UpdateUserAccountStatus(user_id, account_status);
        return new ResponseEntity<>("Users account updated successfully", HttpStatus.OK);
    }


    @PutMapping("/update-request/{request_id}/{request_status}")
    public ResponseEntity<String> updateRequest(@PathVariable long request_id, @PathVariable String request_status){
        adminService.UpdateRequestStatus(request_id, request_status);
        return new ResponseEntity<>("Request updated successfully", HttpStatus.OK);
    }

    @PutMapping("/update-request/{request_id}/{request_status}/{comment}")
    public ResponseEntity<String> updateRequestWithComment(@PathVariable long request_id, @PathVariable String request_status, @PathVariable String comment){
        adminService.UpdateRequestStatusWithComment(request_id, request_status, comment);
        return new ResponseEntity<>("Request updated successfully", HttpStatus.OK);
    }

    @PostMapping("/requests/update-status")
    public ResponseEntity<String> updateRequests(@RequestBody List<UpdateRequestStatusRequestModel> requestModelList){
        adminService.UpdateMultipleRequestStatus(requestModelList);
        return new ResponseEntity<>("Requests updated successfully", HttpStatus.OK);
    }

    @GetMapping("/filter-by-date")
    public ResponseEntity<List<Request>> filterRequestsByDate(@RequestParam String filter){
        List<Request> filteredRequests = adminService.getRequestsByDateFilter(filter);
        return new ResponseEntity<>(filteredRequests, HttpStatus.OK);
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

    @GetMapping("/search/requests")
    public ResponseEntity<Page<Request>> fuzzySearchRequest(@RequestParam String searchTerm, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        return new ResponseEntity<>(adminService.requestsFuzzySearch(searchTerm, pageNumber, pageSize), HttpStatus.OK);
    }
}

