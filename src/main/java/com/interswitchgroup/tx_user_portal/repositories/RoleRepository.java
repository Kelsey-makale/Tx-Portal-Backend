package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
   // Optional<Role> findByRoleRoleName(String role_name);
}
