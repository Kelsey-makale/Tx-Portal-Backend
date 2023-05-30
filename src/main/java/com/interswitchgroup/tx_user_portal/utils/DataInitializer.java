package com.interswitchgroup.tx_user_portal.utils;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.repositories.OrganizationRepository;
import com.interswitchgroup.tx_user_portal.repositories.RightsRepository;
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
    private final RightsRepository rightsRepository;
    private final OrganizationRepository organizationRepository;
    PasswordEncoder passwordEncoder;

    public DataInitializer(OrganizationRepository orderRepository, RoleRepository roleRepository, UserRepository userRepository, RightsRepository rightsRepository, OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder) {
        this.orderRepository = orderRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.rightsRepository = rightsRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        //------------------ROLES & RIGHTS---------------------------------

        Role role1 = new Role(1, "Basic user role", "");
        Role role2 = new Role(2, "Hot card role", "");


        Right right1 =  new Right(1,"PAN Viewer", "");
        Right right2 = new Right(2,"Individual Customer Viewer", "");
        Right right3 = new Right(3, "Token Viewer", "");
        Right right4 = new Right(4, "Card Blocking", "");
        rightsRepository.saveAll(List.of(right1, right2, right3, right4));

        role1.setRights(List.of(right1, right2, right3));
        role2.setRights(List.of(right4));
        roleRepository.saveAll(List.of(role1, role2));


        //------------------ORGANIZATIONS---------------------------------

        Organization org1 = new Organization(1, "Interswitch Group Kenya");
        orderRepository.saveAll(List.of(org1));

        //------------------SUPER ADMIN---------------------------------

        Optional<User> userOptional = userRepository.findUserByEmailAddress("joe.mak@example.com");

        if(userOptional.isEmpty()) {
            User superAdmin = new User();
            String encPass = passwordEncoder.encode("password123");
            Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(1);

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
