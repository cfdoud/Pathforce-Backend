package com.pathdx.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.pathdx.constant.ActionType;
import com.pathdx.constant.AuditAction;
import com.pathdx.dto.requestDto.*;
import com.pathdx.dto.responseDto.*;
import com.pathdx.exception.LabNotFoundException;
import com.pathdx.exception.InvalidRoleException;
import com.pathdx.model.*;
import com.pathdx.model.Role;
import com.pathdx.repository.*;
import com.pathdx.repository.reposervice.OrderMessageRepoService;
import com.pathdx.repository.reposervice.UsersRepoService;
import com.pathdx.service.UserService;
import com.pathdx.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.pathdx.constant.Constants.BODY_Start;
import static com.pathdx.constant.Constants.LENGTH;
import static com.pathdx.constant.UtilConstants.*;


@Component
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    EmailUtil emailUtil;

    @Autowired
    UploadImageUtil uploadImageUtil;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    UsersRepository userRepository;

    @Autowired
    UsersRepoService usersRepoService;

    @Autowired
    LabDetailRepository labDetailRepository;

    @Autowired
    OrderMessageRepoService orderMessageRepoService;

    @Autowired
    GoogleCloudStorageUtil googleCloudStorageUtil;

    @Value("${projectId}")
    private String projectId;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${profileImageObjectName}")
    private String profileImageObjectName;

    @Value("${domain.emails}")
    private String domains;

    @Value("${website.link}")
    private  String webSiteLink;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    OrderMessagesRepository orderMessagesRepository;

    @Autowired
    CaseDetailsRepository caseDetailsRepository;

    @Autowired
    StateRespository stateRespository;

    @Autowired
    DesignateRepository designateRepository;

    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    CaseAllocationRepository caseAllocationRepository;

    @Autowired
    AuditLogUtil auditLogUtil;

    @Override
    public ResponseDto<UserModelReqDto> saveUser(UserModelReqDto userModelDto) {
        ResponseDto<UserModelReqDto> response = new ResponseDto<>();
        String emailId= userModelDto.getEmail().toLowerCase();
//        String[] domainList=domains.split("[,]",0);
        String[] userDomain=emailId.split("[@]",0);
//        domainList[3]=StringUtils.deleteWhitespace(domainList[3]);
        if(domains.contains(userDomain[1])){
                Optional<UserModel> user = userRepository.findUserModelByEmail(userModelDto.getEmail().toLowerCase());
                if (!user.isPresent()) {
                    UserModel userModel = userDtoToModel(userModelDto);
                    userModel.setActive(false);
                    userModel.setPasswordChangeRequired(true);
                    String randomPwd = generateRandomPassword(LENGTH);
                    log.info("random Password: " + randomPwd);
                    String password = emailId.toLowerCase().trim() + randomPwd;
                    log.info("password =  " + password);
                    userModel.setPassword((doHashing(password)));
                    userModel.setEmail(emailId);
                    if (!userModelDto.getProfileImg().isEmpty()) {
                        try {
                            userModel.setProfileImg(uploadImageUtil.uploadUserProfileImage(projectId, bucketName, profileImageObjectName, userModelDto.getProfileImg(), emailId.toLowerCase().trim()));
                        } catch (Exception e) {
                            log.info("Exception on setting profile Image {}", e.getMessage());
                        }
                    } else {
                        userModel.setProfileImg("");
                    }
                    UserModel savedUserModel = userRepository.save(userModel);
                    if (savedUserModel.getId() != null || savedUserModel.getId() > 0) {
                        CaseAllocationConfigModel caseAllocationConfigModel = new CaseAllocationConfigModel();
                        caseAllocationConfigModel.setUserModel(savedUserModel);
                        caseAllocationConfigModel.setMaxNumberOfCases(0);
                        caseAllocationConfigModel.setMaxPendingDays(0);
                        caseAllocationRepository.save(caseAllocationConfigModel);
                    }

                    //for user notification
                    notificationUtil.saveUserNotification(ActionType.NEW_LAB_REVIEWER_REGISTERED, userModel.getEmail(), userModel.getLabDetails().get(0).getLabName());
                    log.info("Current Thread in user Service  " + Thread.currentThread().getName());
                    String firstName = userModel.getFirstName();
                    String msgBody = BODY_Start + commonUtil.capitalizeFirstCharacter(firstName) + BODY + randomPwd +"</br></br> "+webSiteLink+ BODY_END + THANK;
                    EmailModelDto emailModelDto = new EmailModelDto();
                    emailModelDto.setFrom("info@pathforcetech.com");
                    emailModelDto.setTo(userModel.getEmail().toLowerCase());
                    emailModelDto.setSubject(SUBJECT);
                    emailModelDto.setBody(msgBody);
                    try {
                        emailUtil.sendmail(emailModelDto);
                    } catch (MessagingException e) {
                        response.setError(e.getMessage());
                        return response;

                    } catch (IOException e) {
                        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                        response.setError(e.getMessage());
                        return response;
                    }
                } else {
                    String errorMessage = "An account is already registered with your email";
                    response.setError(errorMessage);
                    response.setStatusCode(HttpStatus.IM_USED.value());
                    return response;
                }
                response.setResponse(userModelDto);
                return response;
            }
        else{

        String errorMessage="Invalid User Domain";
        response.setError(errorMessage);
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return response;
        }

    }

    @Override
    public ResponseDto<UserModelReqDto> updateUser(UserModelReqDto userModelDto) {
        ResponseDto<UserModelReqDto> response = new ResponseDto<>();
        String emailId= userModelDto.getEmail().toLowerCase();
        Optional<UserModel> user= userRepository.findUserModelByEmail(emailId);
        if(user.isPresent()) {
            UserModel userModel = userDtoToModelForUpdate(userModelDto,user.get());
            userRepository.save(userModel);
            //for Audit log
            ActionModel actionModel = auditLogUtil.getActions(AuditAction.PROFILE_EDIT);
            Object[] args = {userModel.getEmail()};
            String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
            auditLogUtil.saveAuditLogs(AuditAction.PROFILE_EDIT, userModel.getId(), msg);
            String errorMessage = "User updated";
            response.setError(errorMessage);
            response.setStatusCode(HttpStatus.OK.value());
            return response;
        }

        String errorMessage = "No User found";
        response.setError(errorMessage);
        response.setStatusCode(HttpStatus.IM_USED.value());
        return response;
    }

    private UserModel userDtoToModelForUpdate(UserModelReqDto userModelDto, UserModel userModel) {
        List<Role> userModelRole=userModel.getRoles();
        userModel.setFirstName(userModelDto.getFirstName());
        userModel.setMiddleName(userModelDto.getMiddleName());
        userModel.setLastName(userModelDto.getLastName());
        userModel.setGender(userModelDto.getGender());
        userModel.setNpi(userModelDto.getNpi());
        userModel.setStreetAddress(userModelDto.getStreetAddress());
        userModel.setSearchAddress(userModelDto.getSearchAddress());
        userModel.setCity(userModelDto.getCity());
        userModel.setHomeState(userModelDto.getHomeState());
        userModel.setZip(userModelDto.getZip());
        userModel.setMobilePh(userModelDto.getMobilePh());
        userModel.setHomePh(userModelDto.getHomePh());
        userModel.setEmergencyPh(userModelDto.getEmergencyPh());
        userModel.setEmail(userModelDto.getEmail());

        if (!userModelDto.getProfileImg().isEmpty()) {
            try {
                userModel.setProfileImg(uploadImageUtil.uploadUserProfileImage(projectId, bucketName, profileImageObjectName, userModelDto.getProfileImg(), userModelDto.getEmail().toLowerCase().trim()));
            } catch (Exception e) {
                log.info("Exception on setting profile Image {}", e.getMessage());
            }
        } else {
            userModel.setProfileImg("");
        }

        List<StateModel> stateModels = new ArrayList<>();
        for (StateReqDto stateReqDTO : userModelDto.getStateReqDTOS()){
            Optional<StateModel> stateModelOptional = stateRespository.findById(stateReqDTO.getId());
            if(stateModelOptional.isPresent()){
                stateModels.add(stateModelOptional.get());
            }else{
                log.info("No state is selected");
            }

        }
        userModel.setLicensedStates(stateModels);

        List<LabDetail> labDetails = new ArrayList<>();
        for (LabDetailDto labDetailDto:userModelDto.getAssociatedLab()) {
            Optional<LabDetail> labDetail = labDetailRepository.findByLabid(labDetailDto.getLabId());
            if(labDetail.isPresent()){
                labDetails.add(labDetail.get());
            }else{
                log.info("No Lab is Selected");
            }
        }

        List<DesignationModel> designationModels  = new ArrayList<>();
        for (DesignationReqDto designationReqDTO:userModelDto.getDesignationReqDTOS()) {
            Optional<DesignationModel> designationModel = designateRepository.findById(designationReqDTO.getId());
            if(designationModel.isPresent()){
                designationModels.add(designationModel.get());
            }else{
                log.info("No Designation is Selected");
            }
        }

        List<Role> roles = new ArrayList<>();

        for (RoleDto roleDto :userModelDto.getRoles()) {
            Role role = new Role();
            role.setId(roleDto.getId());
            role.setRoleName(roleDto.getRoleName());
            roles.add(role);
        }
        userModel.setRoles(roles);
        userModel.setDesignation(designationModels);
        userModel.setLabDetails(labDetails);
        userModel.setDegree(userModelDto.getDegree());
        userModel.setCollege(userModelDto.getCollege());
        userModel.setYearOfPassing(userModelDto.getYearOfPassing());
        userModel.setPasswordChangeRequired(userModelDto.isPasswordChangeRequired());
//        userModel.setActive(userModelDto.isActive());

//        for user notification
        if(!(userModelRole.get(0).getId().equals(userModelDto.getRoles().get(0).getId())) && userModelDto.getRoles().get(0).getId() == 2 ){
            notificationUtil.saveUserNotification(ActionType.NEW_ADMIN_REGISTERED, userModel.getEmail(), userModel.getLabDetails().get(0).getLabName());
        }

        return userModel;
    }

    private UserModel userDtoToModel(UserModelReqDto userModelDto) {
        UserModel userModel = new UserModel();

        userModel.setFirstName(userModelDto.getFirstName());
        userModel.setMiddleName(userModelDto.getMiddleName());
        userModel.setLastName(userModelDto.getLastName());
        userModel.setGender(userModelDto.getGender());
        userModel.setNpi(userModelDto.getNpi());
        userModel.setStreetAddress(userModelDto.getStreetAddress());
        userModel.setSearchAddress(userModelDto.getSearchAddress());
        userModel.setCity(userModelDto.getCity());
        userModel.setHomeState(userModelDto.getHomeState());
        userModel.setZip(userModelDto.getZip());
        userModel.setMobilePh(userModelDto.getMobilePh());
        userModel.setHomePh(userModelDto.getHomePh());
        userModel.setEmergencyPh(userModelDto.getEmergencyPh());
        userModel.setEmail(userModelDto.getEmail());

        List<StateModel> stateModels = new ArrayList<>();
        for (StateReqDto stateReqDTO : userModelDto.getStateReqDTOS()){
            Optional<StateModel> stateModelOptional = stateRespository.findById(stateReqDTO.getId());
            if(stateModelOptional.isPresent()){
                stateModels.add(stateModelOptional.get());
            }else{
                log.info("No state is selected");
            }

        }
        userModel.setLicensedStates(stateModels);

        List<LabDetail> labDetails = new ArrayList<>();
        for (LabDetailDto labDetailDto:userModelDto.getAssociatedLab()) {
            Optional<LabDetail> labDetail = labDetailRepository.findByLabid(labDetailDto.getLabId());
            if(labDetail.isPresent()){
                labDetails.add(labDetail.get());
            }else{
                log.info("No Lab is Selected");
            }
        }

        List<DesignationModel> designationModels  = new ArrayList<>();
        for (DesignationReqDto designationReqDTO:userModelDto.getDesignationReqDTOS()) {
            Optional<DesignationModel> designationModel = designateRepository.findById(designationReqDTO.getId());
            if(designationModel.isPresent()){
                designationModels.add(designationModel.get());
            }else{
                log.info("No Designation is Selected");
            }
        }

        List<Role> roles = new ArrayList<>();

        for (RoleDto roleDto :userModelDto.getRoles()) {
            Role role = new Role();
            if(roleDto.getId()==1){
                role.setId(roleDto.getId());
            }else{
                throw new InvalidRoleException("Role Can Not Assign");
            }


            role.setRoleName(roleDto.getRoleName());
            roles.add(role);
        }
        userModel.setRoles(roles);
        userModel.setDesignation(designationModels);
        userModel.setLabDetails(labDetails);
        userModel.setDegree(userModelDto.getDegree());
        userModel.setCollege(userModelDto.getCollege());
        userModel.setYearOfPassing(userModelDto.getYearOfPassing());
        userModel.setPasswordChangeRequired(userModelDto.isPasswordChangeRequired());
        userModel.setActive(userModelDto.isActive());
        return userModel;
    }


    @Override
    public ResponseDto<UserLoginResponseDto> login(UserLoginRequestDto loginRequest) {
        ResponseDto<UserLoginResponseDto>  responseDto = new ResponseDto<>();
        UserLoginResponseDto userLoginResponse = new UserLoginResponseDto();
        //user details matching
        Optional<UserModel> userModel = userRepository.findUserModelByEmail(loginRequest.getEmail().toLowerCase());

        if(userModel.isPresent()){
            UserModel model = userModel.get();
            String userEmail=model.getEmail();
            if(!model.isActive()){
                responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                userLoginResponse.setMessage("User is not active");
                responseDto.setResponse(userLoginResponse);
                return responseDto;
            }
            if(userEmail.split("@")[1].equals("averodx.com") && !loginRequest.getSsoLogin()){
                responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                userLoginResponse.setMessage("User Domain Not Allowed");
                responseDto.setResponse(userLoginResponse);
                return responseDto;
            }
            String loginHash=null;
            if(!loginRequest.getSsoLogin()){
                loginHash = doHashing(loginRequest.getEmail().toLowerCase().trim()+loginRequest.getPassword());
            }

            if(model.getPassword().equals(loginHash)){
                //return Jwt token
                userLoginResponse.setMessage("User Logged In Successfully ");
                try {
                    userLoginResponse.setJwtToken(jwtTokenUtil.get(loginRequest.getEmail().toLowerCase(),loginRequest.getPassword()));
                    userLoginResponse.setFirstName(model.getFirstName());
                    userLoginResponse.setMiddleName(model.getMiddleName());
                    userLoginResponse.setLastName(model.getLastName());
                    // userLoginResponse.setProfileImg(model.getProfileImg());
                    userLoginResponse.setActive(model.isActive());
                    userLoginResponse.setChangePassword(model.isPasswordChangeRequired());
                    List<RoleDto> roleDtos = new ArrayList<>();
                    if(model.getRoles().size()>0){
                        for (Role role : model.getRoles()) {
                            RoleDto roleDto = new RoleDto();
                            roleDto.setId(role.getId());
                            roleDto.setRoleName(role.getRoleName());
                            roleDtos.add(roleDto);
                        }
                    }
                    userLoginResponse.setRoles(roleDtos);
                    if(model.getProfileImg()==null){
                        userLoginResponse.setProfileImg("");
                    }else {
                        try {
                            userLoginResponse.setProfileImg(getProfileImageSignedUrl(projectId, bucketName, profileImageObjectName, model.getEmail()));
                        } catch (Exception e) {
                            log.info("Exception in login generating signedurl profile Image {}", e.getMessage());
                        }
                    }
                    model.setJwtToken(userLoginResponse.getJwtToken());
                    log.info("jwt token:: {}",userLoginResponse.getJwtToken());
                    Date dateTime = new Date();
                    model.setLastLogin(dateTime);
                    userRepository.save(model);
                    //for Audit log
                    ActionModel actionModel = auditLogUtil.getActions(AuditAction.LOGIN_SUCCESS);
                    Object[] args = {model.getEmail()};
                    String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
                    auditLogUtil.saveAuditLogs(AuditAction.LOGIN_SUCCESS, model.getId(), msg);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                responseDto.setSuccessMsg("Success");
                responseDto.setStatusCode(HttpStatus.OK.value());
                responseDto.setResponse(userLoginResponse);
                return responseDto;
            }else{
                //for Audit log
                ActionModel actionModel = auditLogUtil.getActions(AuditAction.LOGIN_FAILED);
                Object[] args = {model.getEmail()};
                String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
                auditLogUtil.saveAuditLogs(AuditAction.LOGIN_FAILED, model.getId(), msg);
                userLoginResponse.setMessage("Invalid UserName or password ");
                responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                responseDto.setResponse(userLoginResponse);
                return responseDto;
            }
        }else{
            responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            userLoginResponse.setMessage("Invalid User Found");
            responseDto.setResponse(userLoginResponse);
            return responseDto;
        }
    }
    @Override
    public ResponseDto<UserLoginResponseDto> samlLogin(UserLoginRequestDto loginRequest) {
        ResponseDto<UserLoginResponseDto>  responseDto = new ResponseDto<>();
        UserLoginResponseDto userLoginResponse = new UserLoginResponseDto();
        Optional<UserModel> userModel = userRepository.findUserModelByEmail(loginRequest.getEmail().toLowerCase());
        if(userModel.isPresent()){
            UserModel model = userModel.get();
            String userEmail=model.getEmail();
            if(!model.isActive()){
                responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                userLoginResponse.setMessage("User is not active");
                responseDto.setResponse(userLoginResponse);
                return responseDto;
            }
            try {
                userLoginResponse.setJwtToken(jwtTokenUtil.get(loginRequest.getEmail().toLowerCase(),loginRequest.getPassword()));
                userLoginResponse.setFirstName(model.getFirstName());
                userLoginResponse.setMiddleName(model.getMiddleName());
                userLoginResponse.setLastName(model.getLastName());
                // userLoginResponse.setProfileImg(model.getProfileImg());
                userLoginResponse.setActive(model.isActive());
                userLoginResponse.setChangePassword(model.isPasswordChangeRequired());
                List<RoleDto> roleDtos = new ArrayList<>();
                if(model.getRoles().size()>0){
                    for (Role role : model.getRoles()) {
                        RoleDto roleDto = new RoleDto();
                        roleDto.setId(role.getId());
                        roleDto.setRoleName(role.getRoleName());
                        roleDtos.add(roleDto);
                    }
                }
                userLoginResponse.setRoles(roleDtos);
                if(model.getProfileImg()==null){
                    userLoginResponse.setProfileImg("");
                }else {
                    try {
                        userLoginResponse.setProfileImg(getProfileImageSignedUrl(projectId, bucketName, profileImageObjectName, model.getEmail()));
                    } catch (Exception e) {
                        log.info("Exception in login generating signedurl profile Image {}", e.getMessage());
                    }
                }
                model.setJwtToken(userLoginResponse.getJwtToken());
                log.info("jwt token:: {}",userLoginResponse.getJwtToken());
                userRepository.save(model);
                //for Audit log
                ActionModel actionModel = auditLogUtil.getActions(AuditAction.LOGIN_SUCCESS);
                Object[] args = {model.getEmail()};
                String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
                auditLogUtil.saveAuditLogs(AuditAction.LOGIN_SUCCESS, model.getId(), msg);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            responseDto.setSuccessMsg("Success");
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setResponse(userLoginResponse);
            return responseDto;
        }

        return responseDto;
    }

    @Override
    public ResponseDto<RefreshTokenResponseDto> getRefreshToken(String email, String password) {
        ResponseDto<RefreshTokenResponseDto>  responseDto = new ResponseDto<>();
        RefreshTokenResponseDto refreshTokenResponse = new RefreshTokenResponseDto();
        Optional<UserModel> userModel = userRepository.findUserModelByEmail(email.toLowerCase());
        if(userModel.isPresent()) {
            UserModel model = userModel.get();
            try {
                refreshTokenResponse.setRefreshToken(jwtTokenUtil.get(email.toLowerCase(), password));
                refreshTokenResponse.setMessge("Request success");
                responseDto.setStatusCode(HttpStatus.OK.value());
                model.setJwtToken(refreshTokenResponse.getRefreshToken());
                userRepository.save(model);
            } catch (UnsupportedEncodingException e) {
                refreshTokenResponse.setMessge("Something Went Wrong");
                responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                log.info(e.getMessage());
            }
            responseDto.setResponse(refreshTokenResponse);
            return responseDto;
        }else {
            refreshTokenResponse.setMessge("Invalid User Found");
            responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            responseDto.setResponse(refreshTokenResponse);
            return responseDto;
        }

    }

    @Override
    public ResponseDto<ChangePasswordResponseDto> changePassword(ChangePasswordDto changePasswordDto) throws IOException {
        ResponseDto<ChangePasswordResponseDto>  responseDto = new ResponseDto<>();
        ChangePasswordResponseDto changePasswordResponse = new ChangePasswordResponseDto();
        EmailModelDto emailModelDto=new EmailModelDto();
        Optional<UserModel> userModel = userRepository.findUserModelByEmail(changePasswordDto.getEmail().toLowerCase());
        if(userModel.isPresent()) {
            UserModel model = userModel.get();
            String currentPasswordHash = doHashing(changePasswordDto.getEmail().toLowerCase().trim() + changePasswordDto.getCurrentPassword());
            if (model.getPassword().equals(currentPasswordHash)) {
                String newPasswordHash = doHashing(changePasswordDto.getEmail().toLowerCase().trim() + changePasswordDto.getNewPassword());
                String confirmPasswordHash = doHashing(changePasswordDto.getEmail().toLowerCase().trim() + changePasswordDto.getConfirmNewPassword());
                if (newPasswordHash.equals(confirmPasswordHash)) {
                    model.setActive(true);
                    model.setPassword(confirmPasswordHash);
                    model.setPasswordChangeRequired(false);
                    userRepository.save(model);

                    //for Audit log
                    ActionModel actionModel = auditLogUtil.getActions(AuditAction.PASSWORD_CHANGED);
                    Object[] args = {model.getEmail()};
                    String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
                    auditLogUtil.saveAuditLogs(AuditAction.PASSWORD_CHANGED, model.getId(), msg);

                    changePasswordResponse.setMessage("Password changed successfully,Login again with new password");
                    emailModelDto.setFrom("info@pathforcetech.com");
                    emailModelDto.setTo(model.getEmail().toLowerCase());
                    emailModelDto.setSubject(CHANGE_PASS_SUBJECT);
                    String firstName=model.getFirstName();
                    emailModelDto.setBody(BODY_Start+commonUtil.capitalizeFirstCharacter(firstName)+BODY_CHANGE_PASS+THANK);
                    try {
                        emailUtil.sendmail(emailModelDto);
                    } catch (MessagingException e) {
                        responseDto.setError(e.getMessage());

                    }
                } else {
                    changePasswordResponse.setMessage("Something Went Wrong");
                    responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    responseDto.setResponse(changePasswordResponse);
                    return responseDto;
                }
            } else {
                changePasswordResponse.setMessage("Invalid Password");
                responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                responseDto.setResponse(changePasswordResponse);
                return responseDto;
            }
        }else {
            changePasswordResponse.setMessage("Invalid User Found");
            responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            responseDto.setResponse(changePasswordResponse);
            return responseDto;
        }
        responseDto.setResponse(changePasswordResponse);
        responseDto.setStatusCode(HttpStatus.OK.value());
        return responseDto;
    }

    @Override
    public ResponseDto forgotPassword(String emailId) {

        ResponseDto<ResponseDto> responseDto = new ResponseDto<>();
        EmailModelDto emailModelDto=new EmailModelDto();
        Optional<UserModel> userOptional = (userRepository.findUserModelByEmail(emailId));

        if (!userOptional.isPresent()) {
            // return "Invalid email id.";
            responseDto.setSuccessMsg("Invalid email id");
            return responseDto;
        } else {
            String randomPwd=generateRandomPassword(8);
            String password = emailId.trim()+randomPwd;
            UserModel userModel = userOptional.get();
            userModel.setPassword(doHashing(password));
            userModel.setPasswordChangeRequired(true);
            userRepository.save(userModel);
            // return "Please Check Your Inbox For New Password.";
            String firstName=userModel.getFirstName();
            String msgBody=BODY_Start+commonUtil.capitalizeFirstCharacter(firstName)+BODY_FORGOT_PASS+randomPwd+"</br></br> "+webSiteLink+THANK;
            emailModelDto.setFrom("info@pathforcetech.com");
            emailModelDto.setTo(userModel.getEmail().toLowerCase());
            emailModelDto.setSubject(SUBJECT_FORGOT_PASS);
            emailModelDto.setBody(msgBody);
            try {
                emailUtil.sendmail(emailModelDto);
            } catch (MessagingException | IOException e) {
                responseDto.setError(e.getMessage());

            }
            responseDto.setSuccessMsg("Please Check Your Inbox For New Password.");
            responseDto.setStatusCode(HttpStatus.OK.value());
            return responseDto;
        }

    }


    public  String generateRandomPassword(int len)
    {
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, len)
                .map(i -> random.nextInt(CHARS.length()))
                .mapToObj(randomIndex -> String.valueOf(CHARS.charAt(randomIndex)))
                .collect(Collectors.joining());
    }


    public static String doHashing(String pass){

        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(pass.getBytes());
            byte[] resultByteArray = messageDigest.digest();
            StringBuilder sb=new StringBuilder();
            for(byte b:resultByteArray){
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return "";
    }

    public String getProfileImageSignedUrl(String projectId, String bucketName, String objectName,String email) {
        log.info(
                "ProjectId- "
                        + projectId
                        + ", BucketName- "
                        + bucketName
                        + ", objectName- "
                        + objectName
                        +",Email-"
                        +email);
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName + email.toLowerCase()+".png");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        boolean flag = googleCloudStorageUtil.isFileExists(objectName + email.toLowerCase()+".png",bucketName);
        String signedUrl = "";
        if(flag) {
            URL url =
                    (storage).signUrl(blobInfo, 24, TimeUnit.HOURS, com.google.cloud.storage.Storage.SignUrlOption.withV4Signature());
            log.info("URL " + url.toString());
            signedUrl = url.toString();
        }
        return signedUrl;
    }

    public Map<Long, List<RoleBasedUserDto>> getAllUsers(String labId) {
        ResponseDto<List<UserModelResponseDto>> response = new ResponseDto<>();
        Optional<LabDetail> labDetail = labDetailRepository.findByLabid(labId);
        if(labDetail.isEmpty())
            throw new LabNotFoundException();

        List<UserModel> users = userRepository.findByLabDetails(labDetail.get());

        return users.stream()
                .map(this::convertToRoleDto)
                .collect(Collectors.groupingBy(RoleBasedUserDto::getRole));



    }

    private RoleBasedUserDto convertToRoleDto(UserModel user) {
        RoleBasedUserDto userModelResDto = new RoleBasedUserDto();
        userModelResDto.setFirstName(user.getFirstName());
        userModelResDto.setMiddleName(user.getMiddleName());
        userModelResDto.setLastName(user.getLastName());
        userModelResDto.setGender(user.getGender());
        userModelResDto.setEmail(user.getEmail());
        userModelResDto.setLastLogin(user.getLastLogin());

        userModelResDto.setActive(user.isActive());
        List<DesignationModel> designation = user.getDesignation();
        List<DesignationResponseDto> lstDesigResDto = new ArrayList<DesignationResponseDto>();
        if (!designation.isEmpty()) {
            for (DesignationModel desig : designation) {
                DesignationResponseDto designationResDto = new DesignationResponseDto();
                designationResDto.setId(desig.getId());
                designationResDto.setName(desig.getDesignationName());
                lstDesigResDto.add(designationResDto);
            }
            userModelResDto.setDesignation(lstDesigResDto);
        }
        userModelResDto.setRole( user.getRoles()
                .stream()
                .map(Role::getId)
                .findFirst()
                .orElse(1L));

        return userModelResDto;

    }


    @Override
    public ResponseDto<UserModelResponseDto> getUserById(String email) {
        ResponseDto<UserModelResponseDto> response = new ResponseDto<>();
        Optional<UserModel> user = userRepository.findUserModelByEmail(email);
        if(user.isPresent()) {
            UserModelResponseDto userModelResDto = convertModelToDto(user.get());
            response.setResponse(userModelResDto);
        }
        return response;
    }

    @Override
    public ResponseDto<List<UserModelResponseDto>> getAllUsersByDesignation(String designation){
        ResponseDto<List<UserModelResponseDto>> response = new ResponseDto<>();
        List<UserModel> users= userRepository.findUserModelByDesignation(designation);
        List<UserModelResponseDto> listOfUserModelDto = convertListModelToDto(users);
        response.setResponse(listOfUserModelDto);
        return response;

    }

    private List<UserModelResponseDto> convertListModelToDto(List<UserModel> users) {
        List<UserModelResponseDto> lstOfuserModelResDto = new ArrayList<UserModelResponseDto>();
        for(UserModel user : users) {
            UserModelResponseDto userModelResDto = convertModelToDto(user);
            lstOfuserModelResDto.add(userModelResDto);
        }
        return lstOfuserModelResDto;
    }

    private UserModelResponseDto convertModelToDto(UserModel user) {
        UserModelResponseDto userModelResDto = new UserModelResponseDto();
        userModelResDto.setId(user.getId());
        userModelResDto.setFirstName(user.getFirstName());
        userModelResDto.setMiddleName(user.getMiddleName());
        userModelResDto.setLastName(user.getLastName());
        userModelResDto.setGender(user.getGender());
        userModelResDto.setNpi(user.getNpi());
        userModelResDto.setStreetAddress(user.getStreetAddress());
        userModelResDto.setSearchAddress(user.getSearchAddress());
        userModelResDto.setCity(user.getCity());
        userModelResDto.setHomeState(user.getHomeState());
        userModelResDto.setZip(user.getZip());
        userModelResDto.setMobilePh(user.getMobilePh());
        userModelResDto.setHomePh(user.getHomePh());
        userModelResDto.setEmergencyPh(user.getEmergencyPh());
        userModelResDto.setEmail(user.getEmail());
        userModelResDto.setDegree(user.getDegree());
        userModelResDto.setCollege(user.getCollege());
        userModelResDto.setYearOfPassing(user.getYearOfPassing());
        userModelResDto.setPasswordChangeRequired(user.isPasswordChangeRequired());
        userModelResDto.setActive(user.isActive());


        if(user.getProfileImg()==null || user.getProfileImg().isEmpty()){
            userModelResDto.setProfileImg("");
        }else {
            try {
                userModelResDto.setProfileImg(getProfileImageSignedUrl(projectId, bucketName, profileImageObjectName, user.getEmail()));
            } catch (Exception e) {
                log.info("Exception in login generating signedurl profile Image {}", e.getMessage());
            }
        }


        List<DesignationModel> designation=user.getDesignation();
        List<DesignationResponseDto> lstDesigResDto=new ArrayList<DesignationResponseDto>();
        if(!designation.isEmpty()){
            for(DesignationModel desig: designation){
                DesignationResponseDto designationResDto = new DesignationResponseDto();
                designationResDto.setId(desig.getId());
                designationResDto.setName(desig.getDesignationName());
                lstDesigResDto.add(designationResDto);
            }
            userModelResDto.setDesignation(lstDesigResDto);
        }

        List<StateModel> states=user.getLicensedStates();
        List<StateResponseDto> lstStateResDto=new ArrayList<StateResponseDto>();
        if(!states.isEmpty()){
            for(StateModel stateM: states){
                StateResponseDto stateResDto=new StateResponseDto();
                stateResDto.setId(stateM.getId());
                stateResDto.setName(stateM.getStateName());
                lstStateResDto.add(stateResDto);
            }
            userModelResDto.setLicensedStates(lstStateResDto);
        }

        List<LabDetail> associatedLabs=user.getLabDetails();
        List<LabResponseDto> lstAssoLabResDto=new ArrayList<LabResponseDto>();
        if(!associatedLabs.isEmpty()){
            for(LabDetail assoLab: associatedLabs){
                LabResponseDto labResponseDto =new LabResponseDto();
                labResponseDto.setLabId(assoLab.getLabid());
                labResponseDto.setLabName(assoLab.getLabName());
                lstAssoLabResDto.add(labResponseDto);
            }
            userModelResDto.setLabDetails(lstAssoLabResDto);
        }

        List<Role> roles = user.getRoles();
        List<RoleResponseDto> lstRolesResDto = new ArrayList<RoleResponseDto>();
        if(!roles.isEmpty()) {
            for (Role role : roles) {
                RoleResponseDto roleResDto = new RoleResponseDto();
                roleResDto.setId(role.getId());
                roleResDto.setRoleName(role.getRoleName());
                lstRolesResDto.add(roleResDto);
            }
            userModelResDto.setRoles(lstRolesResDto);
        }
        return userModelResDto;
    }

    @Override
    public Set<String> getassignUsers(String labId) {

        Set<String> users =  usersRepoService.getUserMailByRoles(labId);

        return users;
    }

    public List<UserModelResponseDto> getUsersByLabId(LabDetail labDetail) {
        List<UserModel> userModels = userRepository.findByLabDetails(labDetail);
        List<UserModelResponseDto> lstUserModelResDto = convertListModelToDto(userModels);
        return lstUserModelResDto;
    }

    @Override
    public String deactivateUser(String labid, String usermail, String status, String email) {
        Long roleId = userRepository.findUserModelByEmail(email)
                .get()
                .getRoles()
                .stream()
                .map(Role::getId)
                .filter(aLong -> aLong==3L || aLong==2L).findFirst().orElse(0L);
        if(roleId==0L) return "loggedIn user doesn't have right to activate/deactivate users";
        Optional<UserModel> userModel = userRepository.findUserModelByEmail(usermail);
        boolean nonematch = userModel
                .map(UserModel::getLabDetails)
                .get()
                .stream().map(LabDetail::getLabid)
                .noneMatch(s -> StringUtils.equalsIgnoreCase(s,labid));
        if(nonematch)throw new LabNotFoundException();
        if(StringUtils.equalsIgnoreCase(status, "deactivate")) {
           Long count = orderMessageRepoService.deactivateUserOpenIssues(labid, userModel.get().getId());
           if(count == 0) {
               userModel.get().setActive(false);
               userRepository.save(userModel.get());
                Optional<LabDetail> labDetail=labDetailRepository.findByLabid(labid);
               //for user notification
               try{
                   notificationUtil.saveUserNotification(ActionType.USER_DEACTIVATED, usermail,labDetail.get().getLabName());
               }catch (Exception e){
                   log.info("Exception occured while saving notification::{}",e);
               }

               return "deactivated user successfully";

           }else{
               return "There are open cases for given user";
           }
        }
        else {
            userModel.get().setActive(true);
            userRepository.save(userModel.get());
            return "activated user successfully";
        }

    }

    @Override
    public List<String> getUsersList(String labid, String email) {
        Optional<UserModel> userModel = userRepository.findUserModelByEmail(email);
        if(!userModel.isPresent()) {
            return Arrays.asList("logged in user is not super admin");
        }
            boolean b = userModel.get()
                    .getRoles()
                    .stream()
                    .map(Role::getId)
                    .noneMatch(aLong -> aLong == 3L || aLong == 2L);


            if (!b) {
              return userRepository.getUsersList(labid);
            } else {
                return Arrays.asList("logged in user is not super admin");
            }

    }

    @Override
    public String addLabTouser(UserLabDto userLabDto) {
       Optional<UserModel> userModel =  userRepository.findUserModelByEmail(userLabDto.getEmail());
        Optional<LabDetail> labDetail = labDetailRepository.findByLabid(userLabDto.getLabId());
       if(userModel.isPresent() && labDetail.isPresent()){
           UserModel userModel1 = userModel.get();
           List<LabDetail> labDetails = userModel1.getLabDetails();
           labDetails.add(labDetail.get());
           userModel1.setLabDetails(labDetails);
           userRepository.save(userModel1);
           return "lab added successfully";
       }
       else{
           return "given user or lab doesn't exist";
       }

    }
}
