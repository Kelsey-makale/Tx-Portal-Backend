package com.interswitchgroup.tx_user_portal.utils;

import com.interswitchgroup.tx_user_portal.entities.Organization;
import com.interswitchgroup.tx_user_portal.entities.Role;
import com.interswitchgroup.tx_user_portal.repositories.OrganizationRepository;
import com.interswitchgroup.tx_user_portal.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Creating dummy data for testing purposes
 */

@Component
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository orderRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(OrganizationRepository orderRepository, RoleRepository roleRepository) {
        this.orderRepository = orderRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Organization org1 = new Organization(1, "Family Bank");
        Organization org2 = new Organization(2, "Interswitch");
        Organization org3 = new Organization(3, "KCB");
        orderRepository.saveAll(Arrays.asList(org1, org2, org3));

        Role role1 = new Role(1, "Basic user role");
        Role role2 = new Role(2, "Hot card role");
        Role role3 = new Role(3, "Cold card role");
        Role role4 = new Role(4, "Monitoring role");
        roleRepository.saveAll(Arrays.asList(role1, role2, role3, role4));

    }
}
