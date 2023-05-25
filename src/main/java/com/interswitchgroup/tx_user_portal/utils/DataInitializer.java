package com.interswitchgroup.tx_user_portal.utils;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.repositories.OrganizationRepository;
import com.interswitchgroup.tx_user_portal.repositories.RoleRepository;
import com.interswitchgroup.tx_user_portal.repositories.UserRepository;
import com.interswitchgroup.tx_user_portal.utils.Enums.UserPermission;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Creating dummy data for testing purposes
 */

@Component
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository orderRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    PasswordEncoder passwordEncoder;

    public DataInitializer(OrganizationRepository orderRepository, RoleRepository roleRepository, UserRepository userRepository, OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder) {
        this.orderRepository = orderRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        //------------------ROLES & RIGHTS---------------------------------

        Role role1 = new Role(1, "Basic user role", "");
        Role role2 = new Role(2, "Hot card role", "");
        Role role3 = new Role(3, "Cool card role", "");
        Role role4 = new Role(4, "Monitoring role", "");


        Right right1 =  new Right("PAN Viewer", "");
        Right right2 = new Right("Individual Customer Viewer", "");
        Right right3 = new Right("Token Viewer", "");
        Right right4 = new Right("Card Blocking", "");


        role1.getRights().add(right1);
        role1.getRights().add(right2);
        role1.getRights().add(right3);

        role2.getRights().add(right4);
        roleRepository.saveAll(List.of(role1, role2, role3, role4));


        //------------------ORGANIZATIONS---------------------------------
        Organization org1 = new Organization(1, "Guardian Bank");
        Organization org2 = new Organization(2, "Family Bank Ltd");
        Organization org3 = new Organization(3, "Mayfair Bank");
        Organization org4 = new Organization(4, "Paramount Bank");
        Organization org5 = new Organization(5, "Sterling Bank");

        orderRepository.saveAll(Arrays.asList(org1, org2, org3, org4, org5));


        //------------------SUPER ADMIN---------------------------------


        Optional<User> userOptional = userRepository.findUserByEmailAddress("joe.mak@example.com");

        if(userOptional.isEmpty()) {
            User superAdmin = new User();
            String encPass = passwordEncoder.encode("password123");
            Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(2);

            superAdmin.setEmailAddress("joe.mak@example.com");
            superAdmin.setPassword(encPass);
            superAdmin.setPermission(UserPermission.ADMIN);
            superAdmin.setDateCreated(LocalDateTime.now());
            superAdmin.setDateUpdated(LocalDateTime.now());

            //2.a Add user details to db
            UserDetails superAdminDetails = new UserDetails(
                    "Joe",
                    "Mak",
                    "+254711486739",
                    "Security Admin",
                    "Shared Tech",
                    "001",
                    organizationOptional.get(),
                    LocalDateTime.now(),
                    LocalDateTime.now());
            superAdminDetails.setEnabled(true);
            superAdmin.setUserDetails(superAdminDetails);

            userRepository.save(superAdmin);
        }
    }
}
