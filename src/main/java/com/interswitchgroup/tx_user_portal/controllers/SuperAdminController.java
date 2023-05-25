package com.interswitchgroup.tx_user_portal.controllers;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.request.AdminSignUpRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.NewOrganizationRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.NewRoleRequestModel;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.services.AdminService;
import com.interswitchgroup.tx_user_portal.services.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final AdminService adminService;

    @Autowired
    public SuperAdminController(SuperAdminService superAdminService, AdminService adminService) {
        this.superAdminService = superAdminService;
        this.adminService = adminService;
    }

    @PutMapping("/update-permission/{user_id}/{permission}")
    public ResponseEntity<String> updatePermission(@PathVariable long user_id, @PathVariable String permission){
        return new ResponseEntity<>("Permission updated successfully", HttpStatus.OK);
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<Page<Request>> getPendingRequests(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<Request> allRequests = adminService.getAllPendingRequests(pageNumber, pageSize);
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<User> allUsers = adminService.getAllUsers(pageNumber, pageSize);
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
    public ResponseEntity<UserResponseModel> addUser(@RequestBody AdminSignUpRequestModel adminSignUpRequestModel){
        superAdminService.createBankAdmin(adminSignUpRequestModel);
        return new ResponseEntity<>( superAdminService.createBankAdmin(adminSignUpRequestModel), HttpStatus.OK);
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<AuditLog>> getAllLogs(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<AuditLog> allLogs = superAdminService.fetchAllLogs(pageNumber, pageSize);
        return new ResponseEntity<>(allLogs, HttpStatus.OK);
    }

    @GetMapping("/search/logs")
    public ResponseEntity<Page<AuditLog>> fuzzySearchLogs(@RequestParam String searchTerm, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        return new ResponseEntity<>(superAdminService.fuzzySearchLogs(searchTerm, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/export/logs")
    public ResponseEntity<Resource> exportLogs(){
        return (superAdminService.exportLogData());
    }

    @PutMapping("/roles/edit/{roleId}/{roleName}/{roleDescription}")
    public ResponseEntity<String> editRole(@PathVariable long roleId, @PathVariable String roleName, @PathVariable String roleDescription){
        superAdminService.editRole(roleId,roleName,roleDescription);
        return new ResponseEntity<>("Role updated successfully", HttpStatus.OK);
    }

    @GetMapping("/search/roles")
    public ResponseEntity<Page<Role>> fuzzySearchRoles(@RequestParam String searchTerm, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        return new ResponseEntity<>(superAdminService.fuzzySearchRoles(searchTerm, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Page<Role>> getRoles(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<Role> allRequests = superAdminService.getAllRoles(pageNumber,pageSize);
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @GetMapping("/organizations")
    public ResponseEntity<Page<OrganizationRoleRights>> getOrganizationData(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<OrganizationRoleRights> allRequests = superAdminService.getAllOrganizations(pageNumber,pageSize);
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @GetMapping("/search/organizations")
    public ResponseEntity<Page<Organization>> fuzzySearchOrgz(@RequestParam String searchTerm, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        return new ResponseEntity<>(superAdminService.fuzzySearchOrgz(searchTerm, pageNumber, pageSize), HttpStatus.OK);
    }
}
