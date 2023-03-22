package com.interswitchgroup.tx_user_portal.repositories;

import com.interswitchgroup.tx_user_portal.entities.Organization;
import com.interswitchgroup.tx_user_portal.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequestRepository  extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {


}
