package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.request.UpdateRequestStatusRequestModel;
import com.interswitchgroup.tx_user_portal.repositories.RequestRepository;
import com.interswitchgroup.tx_user_portal.repositories.RoleRepository;
import com.interswitchgroup.tx_user_portal.repositories.UserDetailsRepository;
import com.interswitchgroup.tx_user_portal.repositories.UserRepository;
import com.interswitchgroup.tx_user_portal.utils.Enums.AccountStatus;
import com.interswitchgroup.tx_user_portal.utils.Enums.RequestStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminService(RequestRepository requestRepository, UserRepository userRepository, UserDetailsRepository userDetailsRepository, RoleRepository roleRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.roleRepository = roleRepository;
    }

    public Page<Request> getAllPendingRequests(int pageNumber, int pageSize) {
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();

            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return requestRepository.findAll(isPendingForAdmin(org_id), pageable);
        }catch(Exception e){
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

    public Page<User> getAllUsers(int pageNumber, int pageSize) {
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            long org_id = currentAdmin.getUserDetails().getOrganization().getOrganization_id();

            Pageable paging = PageRequest.of(pageNumber, pageSize);
            Page<User> pagedResult = userRepository.findAll(forAdmin(org_id), paging);
            return pagedResult;
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
            User userObj = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Request foundRequest = requestOptional.get();
            if(request_status.equals(RequestStatus.APPROVED.name())){

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
            else if(request_status.equals(RequestStatus.REJECTED.name())){
                foundRequest.setRequestStatus(RequestStatus.REJECTED);
                foundRequest.setDateUpdated(LocalDateTime.now());
            }
            else{
                throw new IllegalArgumentException("REQUEST VALUE PASSED IS INCORRECT " + request_status);
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
            if(request_status.equals(RequestStatus.APPROVED.name())){

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
            else if(request_status.equals(RequestStatus.REJECTED.name())){
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

        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        else{
           User user = userOptional.get();
           if(account_status.equals(AccountStatus.ENABLE.name())){
               user.getUserDetails().setVerified(true);
           }
           else if(account_status.equals(AccountStatus.DISABLE.name())){
               user.getUserDetails().setVerified(false);
           }
           else{
               throw new IllegalArgumentException("VALUE PASSED IS INCORRECT " + account_status);
           }
           userRepository.save(user);
        }
    }
}
