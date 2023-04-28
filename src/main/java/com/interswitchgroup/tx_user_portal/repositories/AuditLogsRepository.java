package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AuditLogsRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = "SELECT al FROM AuditLog al " +
            "WHERE al.executedBy LIKE %:searchTerm% " +
            "OR al.logActivity LIKE %:searchTerm% " +
            "OR al.details LIKE %:searchTerm%",
            countQuery = "SELECT al FROM AuditLog al " +
                    "WHERE al.executedBy LIKE %:searchTerm% " +
                    "OR al.logActivity LIKE %:searchTerm% " +
                    "OR al.details LIKE %:searchTerm%")
    Page<AuditLog> searchLogs(@Param("searchTerm") String searchTerm, Pageable pageable);
}
