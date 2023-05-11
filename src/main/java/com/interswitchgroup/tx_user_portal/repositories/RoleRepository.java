package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
   @Query(value = "SELECT r FROM Role r " +
           "WHERE r.role_name = :searchTerm"
   )
   Optional<Role> findByRoleName(@Param("searchTerm")String role_name);

   @Query(value = "SELECT r FROM Role r " +
           "WHERE r.role_id = :searchTerm"
   )
   Optional<Role> findByRoleId(@Param("searchTerm")long roleId);


   @Query(value = "SELECT DISTINCT r FROM Role r " +
           "WHERE r.role_name LIKE %:searchTerm% " +
           "OR r.role_description LIKE %:searchTerm%",
           countQuery =  "SELECT COUNT(DISTINCT r) FROM Role r " +
                   "WHERE r.role_name LIKE %:searchTerm% " +
                   "OR r.role_description LIKE %:searchTerm%")
   Page<Role> searchRoles(@Param("searchTerm")String searchTerm, Pageable pageable);

}
