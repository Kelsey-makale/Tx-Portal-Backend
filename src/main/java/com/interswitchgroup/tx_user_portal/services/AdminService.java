package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.request.AdminNewPasswordRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.UpdateRequestStatusRequestModel;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.repositories.*;
import com.interswitchgroup.tx_user_portal.utils.Enums.AccountStatus;
import com.interswitchgroup.tx_user_portal.utils.Enums.LogActivity;
import com.interswitchgroup.tx_user_portal.utils.Enums.RequestStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final AuditLogsRepository auditLogsRepository;
    private final UserVerificationRepository userVerificationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(RequestRepository requestRepository, UserRepository userRepository, UserDetailsRepository userDetailsRepository, RoleRepository roleRepository, EmailService emailService, AuditLogsRepository auditLogsRepository, UserVerificationRepository userVerificationRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.auditLogsRepository = auditLogsRepository;
        this.userVerificationRepository = userVerificationRepository;
    }




    /**
     * Function to fetch all pending requests
     * If user is an BANK ADMIN
     *  fetch only their organizations pending requests
     * ELSE IF user is SUPER_ADMIN
     *  fetch all requests that have been approved
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Request> getAllPendingRequests(int pageNumber, int pageSize) {
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.findAll(isApproved(), pageable);
            }
            else if (user_permission.equals("BANK_ADMIN")) {
                long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.findAll(isPendingForAdmin(org_id), pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Page<Request> requestsFuzzySearch(String searchTerm, int pageNumber, int pageSize){
        try {
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.searchAllRequests(searchTerm, pageable);
            }
            else if (user_permission.equals("BANK_ADMIN")) {
                long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.searchAllOrganizationRequests(org_id, searchTerm, pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * This method returns a JPA Specification that is used to eagerly fetch the requests marked as PENDING
     **/
    private static Specification<Request> isPendingForAdmin(long orgId) {
        return (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get("requestStatus"), RequestStatus.PENDING),
                    builder.equal(root.get("organizationId"), orgId)
            );
        };
    }

    /**
     * This method returns a JPA Specification that is used to eagerly fetch the requests marked as APPROVED
     **/
    private static Specification<Request> isApproved() {
        return (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get("requestStatus"), RequestStatus.APPROVED)
            );
        };
    }

    /**
     * Function to fetch all users
     *      * If user is an BANK ADMIN
     *      *  fetch only their organizations users
     *      * ELSE IF user is SUPER_ADMIN
     *      *  fetch all users
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<User> getAllUsers(int pageNumber, int pageSize) {
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return userRepository.findAll(pageable);
            }
            else if (user_permission.equals("BANK_ADMIN")) {
                long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();
                Pageable paging = PageRequest.of(pageNumber, pageSize);
                Page<User> pagedResult = userRepository.findAll(forAdmin(org_id), paging);
                return pagedResult;
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns a JPA Specification that is used to eagerly fetch users for a specific organization
     **/
    private static Specification<User> forAdmin(long orgId) {
        return (root, query, builder) -> {
            Join<User, UserDetails> userDetailsJoin = root.join("userDetails");
            Join<UserDetails, Organization> organizationJoin = userDetailsJoin.join("organization");
            return builder.and(
                    builder.equal(organizationJoin.get("organizationId"), orgId)
            );
        };
    }

    /**
     * This function approves/rejects one request at a time.
     * @param requestId
     * @param request_status
     */
    public void UpdateRequestStatus(long requestId, String request_status){
        //check if that request exists on the db
        Optional<Request> requestOptional = requestRepository.findRequestByRequestId(requestId);

        if(requestOptional.isEmpty()){
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        else{
            User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(authenticatedUser.getPermission());
            Request foundRequest = requestOptional.get();

            if(user_permission.equals("ADMIN") ){
                if(request_status.equals(RequestStatus.APPROVED.name())){

                    foundRequest.setRequestStatus(RequestStatus.CLOSED);
                    foundRequest.setDateUpdated(LocalDateTime.now());

                    //update users roles as well
                    Set<Role> requestRoles = foundRequest.getRoles();
                    User requestUser = foundRequest.getUser();
                    Set<Role> myRoles = requestUser.getRoles();

                    //avoiding repetition
                    myRoles.addAll(requestRoles);

                    //save those users roles
                    requestUser.setRoles(myRoles);

                    //send email to bank user and cc bank admin(s).
                    String bankUserEmail = requestUser.getEmailAddress();
                    List<String> bankAdminsEmails = userRepository.getMyAdmins(requestUser.getUserDetails().getOrganization().getOrganization_id());

                    emailService.sendMailWithCC(bankUserEmail,
                            "Request Status",
                            "We are pleased to inform you that your recent request has been approved by the administrator. Please proceed with your desired action.",
                            bankAdminsEmails.toArray(new String[0]));

                    //log activity
                    AuditLog newLog = new AuditLog(
                            LogActivity.REQUEST_CLOSED,
                            authenticatedUser.getEmailAddress(),
                        "Request by user:"+requestUser.getEmailAddress()+" was closed by " +authenticatedUser.getEmailAddress()+ ".",
                            LocalDateTime.now()
                    );
                    auditLogsRepository.save(newLog);

                }
                else if(request_status.equals(RequestStatus.REJECTED.name())){
                    foundRequest.setRequestStatus(RequestStatus.REJECTED);
                    foundRequest.setDateUpdated(LocalDateTime.now());

                    //send email to bank user and cc bank admin(s).
                    User requestUser = foundRequest.getUser();
                    String bankUserEmail = requestUser.getEmailAddress();
                    List<String> bankAdminsEmails = userRepository.getMyAdmins(requestUser.getUserDetails().getOrganization().getOrganization_id());

                    emailService.sendMailWithCC(bankUserEmail,
                            "Request Status",
                            "We regret to inform you that your recent request has been rejected by the InfoSec team. To view the reason for the rejection, please access your request on the portal.",
                            bankAdminsEmails.toArray(new String[0]));

                    //log activity
                    AuditLog newLog = new AuditLog(LogActivity.REQUEST_REJECTED,
                            authenticatedUser.getEmailAddress(),
                            "Request by user:"+requestUser.getEmailAddress()+" was rejected by " +authenticatedUser.getEmailAddress()+ ".",
                            LocalDateTime.now()
                    );
                    auditLogsRepository.save(newLog);

                }
                else{
                    throw new IllegalArgumentException("REQUEST VALUE PASSED IS INCORRECT " + request_status);
                }
            }
            else if (user_permission.equals("BANK_ADMIN")) {
                if(request_status.equals(RequestStatus.APPROVED.name())){
                    foundRequest.setRequestStatus(RequestStatus.APPROVED);
                    foundRequest.setDateUpdated(LocalDateTime.now());
                    foundRequest.setApprover_id(authenticatedUser.getUser_id());

                    //send email to super admin and cc bank user.
                    User requestUser = foundRequest.getUser();
                    String bankUserEmail = requestUser.getEmailAddress();
                    String superAdminEmail = userRepository.getSuperAdmin();
                    List<String> ccRecipient = new ArrayList<>();
                    ccRecipient.add(superAdminEmail);
                    ccRecipient.add(authenticatedUser.getEmailAddress());

                    emailService.sendMailWithCC(bankUserEmail,
                            "Request Status",
                            "We are pleased to inform you that your recent request has been approved by your bank administrator. Current status is pending closure by the InfoSec team.",
                            ccRecipient.toArray(new String[0]));

                    //log activity
                    AuditLog newLog = new AuditLog(
                             LogActivity.REQUEST_APPROVED,
                            authenticatedUser.getEmailAddress(),
                            "Request by user:"+requestUser.getEmailAddress()+" was approved by " +authenticatedUser.getEmailAddress()+ ".",
                            LocalDateTime.now()
                    );
                    auditLogsRepository.save(newLog);


                }
                else if(request_status.equals(RequestStatus.REJECTED.name())){
                    foundRequest.setRequestStatus(RequestStatus.REJECTED);
                    foundRequest.setDateUpdated(LocalDateTime.now());

                    User user = foundRequest.getUser();

                    //send email to bank user.
                    emailService.sendMail(user.getEmailAddress(),
                            "Request Status",
                            "We regret to inform you that your recent request has been rejected by your bank administrator. To view the reason for the rejection, please access your request on the portal."
                    );

                    //log activity
                    AuditLog newLog = new AuditLog(
                            LogActivity.REQUEST_APPROVED,
                            authenticatedUser.getEmailAddress(),
                            "Request by user:"+user.getEmailAddress()+" was rejected by " +authenticatedUser.getEmailAddress()+ ".",
                            LocalDateTime.now()
                    );
                    auditLogsRepository.save(newLog);

                }
                else{
                    throw new IllegalArgumentException("REQUEST VALUE PASSED IS INCORRECT " + request_status);
                }
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }

            requestRepository.save(foundRequest);
        }

    }

    /**
     * This function approves/rejects one request at a time with a comment.
     * @param requestId
     * @param request_status
     */
    public void UpdateRequestStatusWithComment(long requestId, String request_status, String comment){
        //check if that request exists on the db
        Optional<Request> requestOptional = requestRepository.findRequestByRequestId(requestId);
        User userObj = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(requestOptional.isEmpty()){
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        else{
            Request foundRequest = requestOptional.get();
            if(request_status.equals(RequestStatus.REJECTED.name())){
                List<Comment> commentList = new ArrayList<>();
                foundRequest.setRequestStatus(RequestStatus.REJECTED);
                foundRequest.setDateUpdated(LocalDateTime.now());

                //create a comment
                Comment adminComment = new Comment(
                        userObj,
                        comment,
                        LocalDateTime.now()
                );
                commentList.add(adminComment);
                foundRequest.setComment(commentList);

                //send email to bank admin(s) and cc bank user.
                User requestUser = foundRequest.getUser();
                String bankUserEmail = requestUser.getEmailAddress();
                List<String> bankAdminsEmails = userRepository.getMyAdmins(requestUser.getUserDetails().getOrganization().getOrganization_id());

                emailService.sendMailWithCC(bankUserEmail,
                        "Request Status",
                        "We regret to inform you that your recent request has been rejected by your admin. To view the reason for the rejection, please access your request on the portal.",
                        bankAdminsEmails.toArray(new String[0]));

            }
            else{
                throw new IllegalArgumentException("REQUEST VALUE PASSED IS INCORRECT " + request_status);
            }
            requestRepository.save(foundRequest);
        }

    }

    /**
     * Approve/Reject multiple requests at the same time.
     * @param requestModelList
     */
    public void UpdateMultipleRequestStatus(List<UpdateRequestStatusRequestModel> requestModelList){

        //loop through each request
        for(UpdateRequestStatusRequestModel requestModel: requestModelList){
            //check if that request exists on the db
            Optional<Request> requestOptional = requestRepository.findRequestByRequestId(requestModel.getRequest_id());

            if(requestOptional.isEmpty()){
                throw new IllegalArgumentException("Request not found: " + requestModel.getRequest_id());
            }
            else{
                User userObj = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                Request foundRequest = requestOptional.get();
                if(requestModel.getRequest_status().equals(RequestStatus.APPROVED.name())){

                    foundRequest.setRequestStatus(RequestStatus.APPROVED);
                    foundRequest.setDateUpdated(LocalDateTime.now());
                    foundRequest.setApprover_id(userObj.getUser_id());

                    //update users roles as well
                    Set<Role> requestRoles = foundRequest.getRoles();
                    User requestUser = foundRequest.getUser();
                    Set<Role> myRoles = requestUser.getRoles();

                    //avoiding repetition
                    myRoles.addAll(requestRoles);

                    //save those users roles
                    requestUser.setRoles(myRoles);


                }
                else if(requestModel.getRequest_status().equals(RequestStatus.REJECTED.name())){
                    foundRequest.setRequestStatus(RequestStatus.REJECTED);
                    foundRequest.setDateUpdated(LocalDateTime.now());
                }
                else{
                    throw new IllegalArgumentException("REQUEST VALUE PASSED IS INCORRECT " + requestModel.getRequest_status());
                }
                requestRepository.save(foundRequest);
            }
        }
    }

    /**
     * Filter requests by date
     * @return
     */
    public List<Request> getRequestsByDateFilter(String filter) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = null;

        switch (filter) {
            case "TODAY":
                endDate = startDate.with(LocalDateTime.MAX);
                break;
            case "PAST_7_DAYS":
                endDate = startDate;
                startDate = startDate.minusDays(7);
                break;
            case "PAST_30_DAYS":
                endDate = startDate;
                startDate = startDate.minusDays(30);
                break;
            case "PAST_90_DAYS":
                endDate = startDate;
                startDate = startDate.minusDays(90);
                break;
        }

        return requestRepository.findByDateRange(startDate, endDate);
    }


    /**
     * Admin can enable or disable a user from logging in and making requests.
     * @param user_id
     * @param account_status
     */
    public void UpdateUserAccountStatus(long user_id, String account_status) {
        //check if user exists on the db
        Optional<User> userOptional = userRepository.findUserByUserId(user_id);
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        else{
           User user = userOptional.get();
           if(account_status.equals(AccountStatus.ENABLE.name())){
               user.getUserDetails().setEnabled(true);

               //send email to bank user.
               emailService.sendMail(user.getEmailAddress(),
                       "Account Enabled",
                       "Your account has been successfully enabled by your administrator. You can now log in using the credentials you have set.");

               //log activity
               AuditLog newLog = new AuditLog(
                        LogActivity.USER_ENABLED,
                       authenticatedUser.getEmailAddress(),
                       "User:"+user.getEmailAddress()+" was enabled by " +authenticatedUser.getEmailAddress()+ ".",
                       LocalDateTime.now()
               );
               auditLogsRepository.save(newLog);
           }
           else if(account_status.equals(AccountStatus.DISABLE.name())){
               user.getUserDetails().setEnabled(false);

               //log activity
               AuditLog newLog = new AuditLog(
                        LogActivity.USER_DISABLED,
                       authenticatedUser.getEmailAddress(),
                       "User:"+user.getEmailAddress()+" was disabled by " +authenticatedUser.getEmailAddress()+ ".",
                       LocalDateTime.now()
               );
               auditLogsRepository.save(newLog);
           }
           else{
               throw new IllegalArgumentException("VALUE PASSED IS INCORRECT " + account_status);
           }
           userRepository.save(user);
        }
    }

    /**
     * Fetch user based on their ID
     * @param user_id
     * @return
     */
    public Map<String, Object> getSpecificUser(long user_id){

        Optional<User> userOptional = userRepository.findUserByUserId(user_id);
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("user not found:");
        }
        User user = userOptional.get();

        Map<String, Object> response = new HashMap<>();
        response.put("data", user);
        return response;

    }


    public Page<Request> pendingRequestsFuzzySearch(String searchTerm, int pageNumber, int pageSize){
        try {
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.searchApprovedRequests(searchTerm, pageable);
            }
            else if (user_permission.equals("BANK_ADMIN")) {
                long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.searchPendingRequests(org_id, searchTerm, pageable);
            }
            else if(user_permission.equals("BANK_USER")){
                long user_id = currentAdmin.getUser_id();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.searchMyRequests(user_id, searchTerm, pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public UserResponseModel createNewPassword(AdminNewPasswordRequestModel requestModel){
        UserResponseModel responseModel;
        Map<String, Object> response = new HashMap<>();

        try{
            Optional<User> userOptional = userRepository.findUserByEmailAddress(requestModel.getEmail_address());
            User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + requestModel.getEmail_address()));

            Optional<UserVerification> userVerificationOptional = userVerificationRepository.findUserVerificationByUserUserId(user.getUser_id());

            if(userVerificationOptional.isPresent()){
                UserVerification userVerificationObj = userVerificationOptional.get();

                if(userVerificationObj.getOtp_code().equals(requestModel.getVerification_code())){

                    //set their password
                    String encPass = passwordEncoder.encode(requestModel.getNew_password());
                    user.setPassword(encPass);
                    user.getUserDetails().setVerified(true);
                    userRepository.save(user);

                    responseModel = new UserResponseModel(
                            HttpStatus.OK.value(),
                            "Password set password successfully."
                    );
                }
                else{
                    response.put("error", "Invalid OTP");
                    responseModel = new UserResponseModel(
                            HttpStatus.EXPECTATION_FAILED.value(),
                            "Failed to reset user password.",
                            Optional.of(response)
                    );
                }
            }
            else{
                //return record does not exist
                throw new IllegalArgumentException("Record does not exist. User needs to sign up first.");
            }
        }
        catch(IllegalArgumentException e){
            response.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to register user",
                    Optional.of(response)
            );
        }

        return responseModel;
    }


    public Page<Request> getAllRequests(int pageNumber, int pageSize){
        try {
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.getAllRequests(pageable);
            }
            else if (user_permission.equals("BANK_ADMIN")) {
                long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return requestRepository.getAllOrganizationRequests(org_id,pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
