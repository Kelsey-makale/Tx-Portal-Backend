package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.OrganizationRoleRights;
import com.interswitchgroup.tx_user_portal.entities.OrganizationRoleRightsId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrgRoleRightsRepository extends JpaRepository<OrganizationRoleRights, OrganizationRoleRightsId> {

    @Query("SELECT orr FROM OrganizationRoleRights orr " +
            "JOIN  orr.organization " +
            "JOIN  orr.role " +
            "JOIN  orr.right")
    Page<OrganizationRoleRights> findAllORR(Pageable pageable);
}
