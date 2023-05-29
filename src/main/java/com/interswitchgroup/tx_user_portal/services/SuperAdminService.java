package com.interswitchgroup.tx_user_portal.services;

import com.interswitchgroup.tx_user_portal.entities.*;
import com.interswitchgroup.tx_user_portal.models.request.AdminSignUpRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.NewOrganizationRequestModel;
import com.interswitchgroup.tx_user_portal.models.request.NewRoleRequestModel;
import com.interswitchgroup.tx_user_portal.models.response.OrganizationRightsResponseModel;
import com.interswitchgroup.tx_user_portal.models.response.UserResponseModel;
import com.interswitchgroup.tx_user_portal.repositories.*;
import com.interswitchgroup.tx_user_portal.utils.Enums.LogActivity;
import com.interswitchgroup.tx_user_portal.utils.Enums.UserPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SuperAdminService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final EmailService emailService;
    private final AuditLogsRepository auditLogsRepository;
    private final ExportService exportService;
    private final RightsRepository rightsRepository;

    @Autowired
    public SuperAdminService(RequestRepository requestRepository, UserRepository userRepository, UserDetailsRepository userDetailsRepository, RoleRepository roleRepository, OrganizationRepository organizationRepository, EmailService emailService, AuditLogsRepository auditLogsRepository, ExportService exportService, RightsRepository rightsRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.emailService = emailService;
        this.auditLogsRepository = auditLogsRepository;
        this.exportService = exportService;
        this.rightsRepository = rightsRepository;
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Function to add/create a new organization
     */
    public void addNewOrganization(NewOrganizationRequestModel requestModel){

        //check if organization already exists
        String org_name = requestModel.getOrganization_name();
        Optional<Organization> organizationOptional = organizationRepository.findByOrganizationName(org_name);

        if(organizationOptional.isPresent()){
            throw new IllegalArgumentException("ORGANIZATION PROVIDED ALREADY EXISTS " + org_name);
        }
        else{
            Organization newOrg = new Organization();
            List<Role> selectedRoles = new ArrayList<>();
            List<Right> organizationRightsList = new ArrayList<>();

            //first create the organization
            List<NewOrganizationRequestModel.RoleData> orgRoleData = requestModel.getRoles();
            for (NewOrganizationRequestModel.RoleData roleData: orgRoleData) {
                Optional<Role> optionalRole = roleRepository.findById(roleData.getRoleId());
                Role foundRole = optionalRole.orElseThrow(() -> new IllegalArgumentException("Role not found :: "
                +roleData.getRoleId() ));

                selectedRoles.add(foundRole);

                //populate composite key table
                for(Long rightId : roleData.getRightIds()){
                    Optional<Right> optionalRight = rightsRepository.findById(rightId);
                    Right foundRight = optionalRight.orElseThrow(() -> new IllegalArgumentException("Right not found"));

                    organizationRightsList.add(foundRight);

                }
            }

            newOrg.setOrganization_name(org_name);
            newOrg.setRoles(selectedRoles);
            newOrg.setRights(organizationRightsList);

            organizationRepository.save(newOrg);

        }
    }

    public Page<OrganizationRightsResponseModel> getAllOrganizations(int pageNumber,int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                Page<Organization> organizationPage = organizationRepository.findAll(pageable);
                return organizationPage.map(this::mapToOrganizationDTO);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<OrganizationRightsResponseModel.RoleDataModel> fetchMyOrganizationRoles(){
        try{
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            long myOrgId = currentUser.getUserDetails().getOrganization().getOrganizationId();
            Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(myOrgId);

            if(organizationOptional.isPresent()){
                return mapToRolesDTO(organizationOptional.get().getRoles(), organizationOptional.get().getRights());
            }else{
                throw new IllegalArgumentException("Organization not found.");
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    ///---------------------MAPPING METHODS TO DTO---------------------------------
    private OrganizationRightsResponseModel mapToOrganizationDTO(Organization organization) {
        OrganizationRightsResponseModel orgDTO = new OrganizationRightsResponseModel();
        orgDTO.setOrganizationId(organization.getOrganizationId());
        orgDTO.setOrganizationName(organization.getOrganizationName());
        orgDTO.setRoles(mapToRolesDTO(organization.getRoles(), organization.getRights()));

        return orgDTO;
    }

    private List<OrganizationRightsResponseModel.RoleDataModel> mapToRolesDTO(List<Role> roles, List<Right> rights){
        return roles.stream()
                .map(role -> mapToRoleDTO(role, rights))
                .collect(Collectors.toList());
    }

    private OrganizationRightsResponseModel.RoleDataModel mapToRoleDTO(Role role, List<Right> orgRights) {
        OrganizationRightsResponseModel.RoleDataModel roleDTO = new OrganizationRightsResponseModel.RoleDataModel();
        roleDTO.setRoleId(role.getRole_id());
        roleDTO.setRoleName(role.getRole_name());
        roleDTO.setRights(convertRightsToDTO(role.getRights(), orgRights));
        return roleDTO;
    }

    private List<OrganizationRightsResponseModel.RightDataModel> convertRightsToDTO(List<Right> rights,List<Right> orgRights) {
        System.out.println("ROLE RIGHTS:: " + rights.size());
        System.out.println("ORG RIGHTS:: " + orgRights.size());

        return rights.stream()
                .flatMap(right -> orgRights.stream()
                        .filter(orgRight -> orgRight.getRight_name().equals(right.getRight_name()))
                        .map(this::convertRightToDTO)
                )
                .collect(Collectors.toList());
    }

    private OrganizationRightsResponseModel.RightDataModel convertRightToDTO(Right right) {
        OrganizationRightsResponseModel.RightDataModel rightDTO = new OrganizationRightsResponseModel.RightDataModel();
        rightDTO.setRightId(right.getRight_id());
        rightDTO.setRightName(right.getRight_name());
        return rightDTO;
    }

    ///---------------------------------------------------------------------------

    public Page<OrganizationRightsResponseModel> fuzzySearchOrgz(String searchTerm,int pageNumber,int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                Page<Organization> organizationPage = organizationRepository.searchAllOrganizations(searchTerm, pageable);
                return organizationPage.map(this::mapToOrganizationDTO);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Page<Role> getAllRoles(int pageNumber, int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return roleRepository.findAll(pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function to add/ create a new role
     */
    public void addNewRole(NewRoleRequestModel requestModel){
        //check if role already exists
        String role_name = requestModel.getRole_name();
        Optional<Role> roleOptional = roleRepository.findByRoleName(role_name);

        if(roleOptional.isPresent()){
            throw new IllegalArgumentException("ROLE PROVIDED ALREADY EXISTS " + role_name);
        }else{
            Role newRole = new Role();
            newRole.setRole_name(role_name);

            for(String rightName : requestModel.getRole_rights()){
                Right newRight = new Right();
                newRight.setRight_name(rightName);
                newRight.setRight_description("");
                rightsRepository.save(newRight);

                newRole.getRights().add(newRight);
            }
            newRole.setRole_description("");
            roleRepository.save(newRole);
        }
    }

    /**
     * Function to edit a role.
     *
     * @param role_id - role ID (INT)
     * @param roleName - role Name (String)
     * @param roleDescription -role Description (String)
     */
    public void editRole(long role_id, String roleName, String roleDescription){
        Optional<Role> roleOptional = roleRepository.findByRoleId(role_id);

        if(roleOptional.isEmpty()){
            throw new IllegalArgumentException("Role not found");
        }
        else{
            Role role = roleOptional.get();
            role.setRole_name(roleName);
            role.setRole_description(roleDescription);

            roleRepository.save(role);
        }
    }

    public Page<Role> fuzzySearchRoles(String searchTerm, int pageNumber, int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return roleRepository.searchRoles(searchTerm, pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function to create a bank admin.
     * For now their password is their office number.
     */
    public UserResponseModel createBankAdmin(AdminSignUpRequestModel userSignUpRequestModel){
        UserResponseModel responseModel;
        Map<String, Object> responseBody = new HashMap<>();
        try {
            Optional<User> userOptional = userRepository.findUserByEmailAddress(userSignUpRequestModel.getEmail_address());
            Optional<Organization> organizationOptional = organizationRepository.findByOrganizationId(userSignUpRequestModel.getOrganization_id());
            User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userOptional.isPresent()) {
                System.out.println("USER ALREADY EXISTS");
                throw new IllegalArgumentException("A user with this email already exists: " + userSignUpRequestModel.getEmail_address());
            }
            if (organizationOptional.isEmpty()) {
                throw new IllegalArgumentException("Organization not found: " + userSignUpRequestModel.getOrganization_id());
            }
            else {

                String oneTimePassword = String.valueOf(UUID.randomUUID()).substring(0,7);
                System.out.println("ONE TIME PASSWORD::"+oneTimePassword);
                String encPass = passwordEncoder.encode(oneTimePassword);
                User newUser = new User();

                List<Role> fetchedRoles = roleRepository.findAllById(userSignUpRequestModel.getRoleIds());
                Set<Role> set = new HashSet<>(fetchedRoles);

                newUser.setEmailAddress(userSignUpRequestModel.getEmail_address());
                newUser.setPassword(encPass);
                newUser.setRoles(set);
                newUser.setPermission( UserPermission.BANK_ADMIN);
                newUser.setDateCreated(LocalDateTime.now());
                newUser.setDateUpdated(LocalDateTime.now());

                //2.Add user details to db
                UserDetails newUserDetails = new UserDetails(
                        userSignUpRequestModel.getFirst_name(),
                        userSignUpRequestModel.getSecond_name(),
                        userSignUpRequestModel.getPhone_number(),
                        userSignUpRequestModel.getDesignation(),
                        userSignUpRequestModel.getDepartment(),
                        userSignUpRequestModel.getOffice_number(),
                        organizationOptional.get(),
                        LocalDateTime.now(),
                        LocalDateTime.now());
                newUserDetails.setEnabled(true);

                newUser.setUserDetails(newUserDetails);
                userRepository.save(newUser);

                //3. Send user email containing the OTP
                emailService.sendMail(newUser.getEmailAddress(),
                        "Account Created",
                        "Your account has been successfully created on the TX User & Role Management Portal. Your login credentials are your email address and your One time password is: "+ oneTimePassword );

                //log activity
                System.out.println("AUTHENTICATED USER::"+ authenticatedUser.toString());

                AuditLog newLog = new AuditLog(
                        LogActivity.ADMIN_CREATED,
                        authenticatedUser.getEmailAddress(),
                        "Bank admin:"+newUser.getEmailAddress()+" was created by " +authenticatedUser.getEmailAddress()+ ".",
                        LocalDateTime.now());
                auditLogsRepository.save(newLog);
            }

            responseModel = new UserResponseModel(
                    HttpStatus.OK.value(),
                    "User registered Successfully"
            );
        }
        catch(IllegalArgumentException e){
            responseBody.put("error", e.getMessage());
            responseModel = new UserResponseModel(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Failed to register user",
                    Optional.of(responseBody)
            );
        }
        return responseModel;
    }

    public Page<AuditLog> fetchAllLogs(int pageNumber, int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return auditLogsRepository.findAll(pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Page<AuditLog> fuzzySearchLogs(String searchTerm, int pageNumber, int pageSize){
        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                return auditLogsRepository.searchLogs(searchTerm, pageable);
            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<Resource> exportLogData(){
        List<AuditLog> data = new ArrayList<>();

        try{
            User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_permission = String.valueOf(currentAdmin.getPermission());

            if(user_permission.equals("ADMIN") ){
                data = auditLogsRepository.findAll();
                ByteArrayInputStream excelData = exportService.convertToExcel(data);


                // Set the response headers for the Excel file download
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename("data.xlsx").build());

                // Return the Excel file as a ResponseEntity
                InputStreamResource bodyData = new InputStreamResource(excelData);

                ResponseEntity<Resource> resource = ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"data.xlsx")
                        .body(bodyData);

                return resource;

            }
            else{
                throw new IllegalArgumentException("User is not authorized to make this request");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
