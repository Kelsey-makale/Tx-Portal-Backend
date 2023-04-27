package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.request.AdminSignUpRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.NewOrganizationRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.NewRoleRequestModel;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.repositories.*;
import com.interswitchgroup.tx_user_portal.utils.Enums.UserPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SuperAdminService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final EmailService emailService;
    private final AuditLogsRepository auditLogsRepository;

    @Autowired
    public SuperAdminService(RequestRepository requestRepository, UserRepository userRepository, UserDetailsRepository userDetailsRepository, RoleRepository roleRepository, OrganizationRepository organizationRepository, EmailService emailService, AuditLogsRepository auditLogsRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.emailService = emailService;
        this.auditLogsRepository = auditLogsRepository;
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Function to add/ create a new organization
     */
    public void addNewOrganization(NewOrganizationRequestModel requestModel){

        //check if organization already exists
        String org_name = requestModel.getOrganization_name();
        Optional<Organization> organizationOptional = organizationRepository.findByOrganizationName(org_name);

        if(organizationOptional.isPresent()){
            throw new IllegalArgumentException("ORGANIZATION PROVIDED ALREADY EXISTS " + org_name);
        }else{
            Organization newOrg = new Organization();
            newOrg.setOrganization_name(org_name);
            organizationRepository.save(newOrg);
        }
    }

    /**
     * Function to add/ create a new role
     */
    public void addNewRole(NewRoleRequestModel requestModel){
/*
        //check if role already exists
        String role_name = requestModel.getRole_name();
        Optional<Role> roleOptional = roleRepository.findByRoleRoleName(role_name);

        if(roleOptional.isPresent()){
            throw new IllegalArgumentException("ROLE PROVIDED ALREADY EXISTS " + role_name);
        }else{
            Role newRole = new Role();
            newRole.setRole_name(role_name);
            roleRepository.save(newRole);
        }

 */
    }

    /**
     * Function to create a bank admin.
     * For now their password is their office number.
     */
    public UserResponseModel createBankAdmin(AdminSignUpRequestModel userSignUpRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();
        try {
            Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignUpRequestModel.getEmail_address());
            Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(userSignUpRequestModel.getOrganization_id());

            if (userOptional.isPresent()) {
                System.out.println("USER ALREADY EXISTS");
                throw new IllegalArgumentException("A user with this email already exists: " + userSignUpRequestModel.getEmail_address());
            }
            if (organizationOptional.isEmpty()) {
                throw new IllegalArgumentException("Organization not found: " + userSignUpRequestModel.getOrganization_id());
            }
            else {
                //todo:Change default password from office number to sth else
                String encPass = passwordEncoder.encode(userSignUpRequestModel.getOffice_number());
                User newUser = new User();

                List<Role> fetchedRoles = roleRepository.findAllById(userSignUpRequestModel.getRoleIds());
                Set<Role> set = new HashSet<>(fetchedRoles);

                newUser.setEmailAddress(userSignUpRequestModel.getEmail_address());
                newUser.setPassword(encPass);
                newUser.setRoles(set);
                newUser.setPermission( UserPermission.BANK_ADMIN);
                newUser.setDateCreated(LocalDateTime.now());
                newUser.setDateUpdated(LocalDateTime.now());


                //2.a Add user details to db
                UserDetails newUserDetails = new UserDetails(
                        userSignUpRequestModel.getFirst_name(),
                        userSignUpRequestModel.getSecond_name(),
                        userSignUpRequestModel.getPhone_number(),
                        userSignUpRequestModel.getDesignation(),
                        userSignUpRequestModel.getDepartment(),
                        userSignUpRequestModel.getOffice_number(),
                        organizationOptional.get(),
                        LocalDateTime.now(),
                        LocalDateTime.now());
                newUserDetails.setVerified(true);

                newUser.setUserDetails(newUserDetails);
                userRepository.save(newUser);

                //3. Send user email containing the OTP
                emailService.sendMail(newUser.getEmailAddress(),
                        "Account Created",
                        "Your account has been successfully created on the TX User & Role Management Portal. Reach out to the InfoSec team to get your login credentials.");

            }

            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "User registered Successfully"
            );
        }
        catch(IllegalArgumentException e){
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to register user",
                    Optional.of(responseBody)
            );
        }
        return responseModel;
    }

    public Page<AuditLog> fetchAllLogs(int pageNumber, int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return auditLogsRepository.findAll(pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Page<AuditLog> fuzzySearchLogs(String searchTerm, int pageNumber, int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return auditLogsRepository.searchLogs(searchTerm, pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



}
