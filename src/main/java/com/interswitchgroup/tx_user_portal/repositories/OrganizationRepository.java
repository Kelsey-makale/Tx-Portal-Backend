package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.Organization;
import com.interswitchgroup.tx_user_portal.entities.Role;
import com.interswitchgroup.tx_user_portal.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByOrganizationId(long organization_id);
    Optional<Organization> findByOrganizationName(String organization_name);

    @Query(value = "SELECT o FROM Organization o " +
            "LEFT JOIN FETCH o.roles or " +
            "WHERE o.organizationName LIKE %:searchTerm% " +
            "OR or.role_name LIKE %:searchTerm%",
            countQuery = "SELECT o FROM Organization o " +
                    "LEFT JOIN FETCH o.roles or " +
                    "WHERE o.organizationName LIKE %:searchTerm% " +
                    "OR or.role_name LIKE %:searchTerm%")
    Page<Organization> searchAllOrganizations(@Param("searchTerm") String searchTerm, Pageable pageable);
}
