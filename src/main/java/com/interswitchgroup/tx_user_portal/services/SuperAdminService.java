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

@Service
public class SuperAdminService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public SuperAdminService(RequestRepository requestRepository, UserRepository userRepository, UserDetailsRepository userDetailsRepository, RoleRepository roleRepository) {
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


    public Page<User> getAllUsers(int pageNumber, int pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<User> pagedResult = userRepository.findAll(paging);
        return pagedResult;
    }

}
