package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AuditLogsRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = "SELECT al FROM AuditLog al " +
            "JOIN FETCH al.user u " +
            "JOIN u.userDetails ud " +
            "WHERE u.emailAddress LIKE %:searchTerm%",
            countQuery = "SELECT al FROM AuditLog al " +
                    "JOIN FETCH al.user u " +
                    "JOIN u.userDetails ud " +
                    "WHERE u.emailAddress LIKE %:searchTerm%"
    )
    Page<AuditLog> searchLogs(@Param("searchTerm") String searchTerm, Pageable pageable);
}
