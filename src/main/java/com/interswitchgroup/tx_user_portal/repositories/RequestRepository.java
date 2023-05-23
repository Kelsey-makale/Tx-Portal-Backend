package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.Request;
import com.interswitchgroup.tx_user_portal.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository  extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {
    Optional<Request> findRequestByRequestId(long request_id);

    @Query("SELECT r FROM Request r WHERE r.dateCreated BETWEEN :startDate AND :endDate")
    List<Request> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query(value = "DELETE FROM Request r " +
            "WHERE r.requestId =:request_id")
    void deleteRequestById(@Param("request_id")long request_id);

    @Query(value = "SELECT r FROM Request r " +
            "JOIN FETCH r.user u " +
            "JOIN u.userDetails ud " +
            "LEFT JOIN FETCH r.roles rl " +
            "WHERE r.requestStatus = 'APPROVED' " +
            "AND(u.emailAddress LIKE %:searchTerm% " +
            "OR ud.firstName LIKE %:searchTerm% " +
            "OR ud.secondName LIKE %:searchTerm% " +
            "OR rl.role_name LIKE %:searchTerm%)",
            countQuery =  "SELECT r FROM Request r " +
                    "JOIN FETCH r.user u " +
                    "JOIN u.userDetails ud " +
                    "LEFT JOIN FETCH r.roles rl " +
                    "WHERE r.requestStatus = 'APPROVED' " +
                    "AND(u.emailAddress LIKE %:searchTerm% " +
                    "OR ud.firstName LIKE %:searchTerm% " +
                    "OR ud.secondName LIKE %:searchTerm% " +
                    "OR rl.role_name LIKE %:searchTerm%)")
    Page<Request> searchApprovedRequests(@Param("searchTerm")String searchTerm, Pageable pageable);


    @Query(value = "SELECT r FROM Request r " +
            "JOIN FETCH r.user u " +
            "JOIN u.userDetails ud " +
            "LEFT JOIN FETCH r.roles rl " +
            "WHERE r.requestStatus = 'PENDING' " +
            "AND r.organizationId = :orgId " +
            "AND(u.emailAddress LIKE %:searchTerm% " +
            "OR ud.firstName LIKE %:searchTerm% " +
            "OR ud.secondName LIKE %:searchTerm% " +
            "OR rl.role_name LIKE %:searchTerm%)",
            countQuery =  "SELECT r FROM Request r " +
                    "JOIN FETCH r.user u " +
                    "JOIN u.userDetails ud " +
                    "LEFT JOIN FETCH r.roles rl " +
                    "WHERE r.requestStatus = 'PENDING' " +
                    "AND r.organizationId = :orgId " +
                    "AND(u.emailAddress LIKE %:searchTerm% " +
                    "OR ud.firstName LIKE %:searchTerm% " +
                    "OR ud.secondName LIKE %:searchTerm% " +
                    "OR rl.role_name LIKE %:searchTerm%)")
    Page<Request> searchPendingRequests(@Param("orgId") long orgId, @Param("searchTerm")String searchTerm, Pageable pageable);


    @Query(value = "SELECT r FROM Request r " +
            "JOIN FETCH r.user u " +
            "JOIN u.userDetails ud " +
            "LEFT JOIN FETCH r.roles rl " +
            "WHERE u.userId = :userId " +
            "AND(u.emailAddress LIKE %:searchTerm% " +
            "OR ud.firstName LIKE %:searchTerm% " +
            "OR ud.secondName LIKE %:searchTerm% " +
            "OR rl.role_name LIKE %:searchTerm%)",
            countQuery =  "SELECT r FROM Request r " +
                    "JOIN FETCH r.user u " +
                    "JOIN u.userDetails ud " +
                    "LEFT JOIN FETCH r.roles rl " +
                    "WHERE u.userId = :userId " +
                    "AND(u.emailAddress LIKE %:searchTerm% " +
                    "OR ud.firstName LIKE %:searchTerm% " +
                    "OR ud.secondName LIKE %:searchTerm% " +
                    "OR rl.role_name LIKE %:searchTerm%)")
    Page<Request> searchMyRequests(@Param("userId") long userId, @Param("searchTerm")String searchTerm, Pageable pageable);


    @Query("SELECT r FROM Request r " +
            "JOIN r.user u " +
            "WHERE u.userId = :userId " +
            "AND r.requestStatus = 'PENDING'")
    List<Request> getAllMyPendingRequests(@Param("userId") long userId);

}
