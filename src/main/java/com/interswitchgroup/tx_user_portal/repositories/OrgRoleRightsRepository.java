package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.OrganizationRoleRights;
import com.interswitchgroup.tx_user_portal.entities.OrganizationRoleRightsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgRoleRightsRepository extends JpaRepository<OrganizationRoleRights, OrganizationRoleRightsId> {
}
