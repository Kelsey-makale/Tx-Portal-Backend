package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.User;
import com.interswitchgroup.tx_user_portal.models.request.CancelRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.UserEditRoleRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.UserRoleRequestModel;
import com.interswitchgroup.tx_user_portal.models.response.OrganizationRightsResponseModel;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.services.GenericService;
import com.interswitchgroup.tx_user_portal.services.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/user")
public class UserController {

    private final GenericService genericService;
    private final SuperAdminService superAdminService;

    @Autowired
    public UserController(GenericService genericService, SuperAdminService superAdminService) {
        this.genericService = genericService;
        this.superAdminService = superAdminService;
    }

    @GetMapping("/roles")
    public ResponseEntity<UserResponseModel> getRoleData(){
        return new ResponseEntity<>(genericService.getRoleData(), HttpStatus.OK);
    }

    @GetMapping("my-roles")
    public ResponseEntity<List<OrganizationRightsResponseModel.RoleDataModel>> getMyRoles(){
        return new ResponseEntity<>(superAdminService.fetchMyOrganizationRoles(), HttpStatus.OK);
    }

    @PostMapping("/request")
    public ResponseEntity<UserResponseModel> makePermissionRequest(@RequestBody UserRoleRequestModel requestModel){
        UserResponseModel responseModel = genericService.makeRequest(requestModel);
        return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getStatus()));
    }
    @PostMapping("/request/edit")
    public ResponseEntity<UserResponseModel> editPermissionRequest(@RequestBody UserEditRoleRequestModel requestModel){
        UserResponseModel userResponseModel =genericService.editRequest(requestModel);
        return new ResponseEntity<>( userResponseModel,  HttpStatus.valueOf(userResponseModel.getStatus()));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<Request>> getPendingRequests(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
       Page<Request> allRequests = genericService.getMyRequests(pageNumber,pageSize);
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @GetMapping("/search/users")
    public ResponseEntity<Page<User>> fuzzySearchUser(@RequestParam String searchTerm, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        return new ResponseEntity<>(genericService.usersFuzzySearch(searchTerm, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/request/cancel")
    public ResponseEntity<UserResponseModel> deleteRequest(@RequestBody CancelRequestModel requestModel){
        UserResponseModel userResponseModel = genericService.cancelRequest(requestModel);
        return new ResponseEntity<>(userResponseModel, HttpStatus.valueOf(userResponseModel.getStatus()));
    }

}
