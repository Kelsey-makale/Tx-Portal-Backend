package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.User;
import com.interswitchgroup.tx_user_portal.repositories.RequestRepository;
import com.interswitchgroup.tx_user_portal.repositories.RoleRepository;
import com.interswitchgroup.tx_user_portal.repositories.UserDetailsRepository;
import com.interswitchgroup.tx_user_portal.repositories.UserRepository;
import com.interswitchgroup.tx_user_portal.utils.Enums.RequestStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return requestRepository.findAll(fetchRolesAndUser().and(isPending()), pageable);
    }

    /**
      This method returns a JPA Specification that is used to eagerly
      fetch the associated user and role entities for each request
     **/
    private static Specification<Request> fetchRolesAndUser() {
        return (root, query, builder) -> {
            // fetch user
            root.fetch("user", JoinType.LEFT);
            // fetch role using join
            root.fetch("roleIds", JoinType.LEFT);
            // return the root entity
            query.distinct(true);
            return builder.conjunction();
        };
    }

    /**
     * This method returns a JPA Specification that is used to eagerly fetch the requests marked as PENDING
     **/
    private static Specification<Request> isPending() {
        return (root, query, builder) -> builder.equal(root.get("requestStatus"), RequestStatus.PENDING);
    }

    public Map<String, Object> getSpecificUser(long user_id){

        Optional<User> userOptional = userRepository.findUserByUserId(user_id);
        User user = userOptional.get();

        Map<String, Object> response = new HashMap<>();
        response.put("data", user);
        return response;

    }

    public Page<User> getAllUsers(int pageNumber, int pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<User> pagedResult = userRepository.findAll(paging);
        return pagedResult;
    }

    public void UpdateRequestStatus(long requestId, String request_status){
        //check if that request exists on the db
        Optional<Request> requestOptional = requestRepository.findRequestByRequestId(requestId);

        if(requestOptional.isEmpty()){
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        else{
            Request foundRequest = requestOptional.get();
            if(request_status.equals(RequestStatus.APPROVED.name())){

                foundRequest.setRequestStatus(RequestStatus.APPROVED);
                foundRequest.setDateUpdated(LocalDateTime.now());

                //update users roles as well
                List<Integer> requested_roles = foundRequest.getRoleIds();
                User requestUser = foundRequest.getUser();
                List<Integer> users_current_roles = requestUser.getRoleIds();

                //avoiding repetition
                if(!users_current_roles.contains(requested_roles)){
                    users_current_roles.addAll(requested_roles);
                }
                //save those users roles
                requestUser.setRoleIds(users_current_roles);
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
}
