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

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findUserByEmailAddress (String emailAddress);
    Optional<User> findUserByUserId(long user_id);

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
    Page<User> searchByMultipleColumns(@Param("searchTerm") String searchTerm, Pageable pageable);

}
