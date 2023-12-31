package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findUserByEmailAddress (String emailAddress);
    Optional<User> findUserByUserId(long user_id);


    @Query(value = "SELECT u.emailAddress FROM User u " +
            "WHERE u.permission = 'ADMIN'"
    )
    String getSuperAdmin();


    @Query(value = "SELECT u.emailAddress FROM User u " +
            "LEFT JOIN u.userDetails ud " +
            "LEFT JOIN ud.organization o " +
            "WHERE u.permission = 'BANK_ADMIN'"+
            "AND o.organizationId = :orgId"
    )
    List<String> getMyAdmins(@Param("orgId")long orgId);


    @Query(value = "SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.userDetails ud " +
            "LEFT JOIN FETCH u.roles r " +
            "WHERE u.emailAddress LIKE %:searchTerm% " +
            "OR ud.firstName LIKE %:searchTerm% " +
            "OR ud.secondName LIKE %:searchTerm% " +
            "OR ud.phoneNumber LIKE %:searchTerm% " +
            "OR ud.designation LIKE %:searchTerm% " +
            "OR ud.department LIKE %:searchTerm% " +
            "OR r.role_name LIKE %:searchTerm% " +
            "OR ud.officeNumber LIKE %:searchTerm%",
            countQuery = "SELECT COUNT(DISTINCT u) FROM User u " +
                    "LEFT JOIN u.userDetails ud " +
                    "LEFT JOIN u.roles r " +
                    "WHERE u.emailAddress LIKE %:searchTerm% " +
                    "OR ud.firstName LIKE %:searchTerm% " +
                    "OR ud.secondName LIKE %:searchTerm% " +
                    "OR ud.phoneNumber LIKE %:searchTerm% " +
                    "OR ud.designation LIKE %:searchTerm% " +
                    "OR ud.department LIKE %:searchTerm% " +
                    "OR r.role_name LIKE %:searchTerm% " +
                    "OR ud.officeNumber LIKE %:searchTerm%")
    Page<User> searchAllUsers(@Param("searchTerm") String searchTerm, Pageable pageable);


    @Query(value = "SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.userDetails ud " +
            "LEFT JOIN ud.organization o " +
            "LEFT JOIN u.roles r " +
            "WHERE o.organizationId = :orgId " +
            "AND(u.emailAddress LIKE %:searchTerm% " +
            "OR ud.firstName LIKE %:searchTerm% " +
            "OR ud.secondName LIKE %:searchTerm% " +
            "OR ud.phoneNumber LIKE %:searchTerm% " +
            "OR ud.designation LIKE %:searchTerm% " +
            "OR ud.department LIKE %:searchTerm% " +
            "OR r.role_name LIKE %:searchTerm% " +
            "OR ud.officeNumber LIKE %:searchTerm%)",
            countQuery = "SELECT COUNT(DISTINCT u) FROM User u " +
                    "LEFT JOIN u.userDetails ud " +
                    "LEFT JOIN ud.organization o " +
                    "LEFT JOIN u.roles r " +
                    "WHERE o.organizationId = :orgId " +
                    "AND(u.emailAddress LIKE %:searchTerm% " +
                    "OR ud.firstName LIKE %:searchTerm% " +
                    "OR ud.secondName LIKE %:searchTerm% " +
                    "OR ud.phoneNumber LIKE %:searchTerm% " +
                    "OR ud.designation LIKE %:searchTerm% " +
                    "OR ud.department LIKE %:searchTerm% " +
                    "OR r.role_name LIKE %:searchTerm% " +
                    "OR ud.officeNumber LIKE %:searchTerm%)")
    Page<User> searchMyOrgUsers(@Param("orgId") long orgId, @Param("searchTerm") String searchTerm, Pageable pageable);

}
