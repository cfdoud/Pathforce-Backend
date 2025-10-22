package com.pathdx.service.impl;

import com.pathdx.constant.ActionType;
import com.pathdx.constant.AuditAction;
import com.pathdx.constant.CommonConstants;
import com.pathdx.dto.requestDto.CaseToUserDto;
import com.pathdx.dto.requestDto.EmailModelDto;
import com.pathdx.dto.requestDto.UserConfigReqDto;
import com.pathdx.dto.requestDto.UserNotificationDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.*;
import com.pathdx.repository.CaseAllocationRepository;
import com.pathdx.repository.LabDetailRepository;
import com.pathdx.repository.OrderMessagesRepository;
import com.pathdx.repository.UsersRepository;
import com.pathdx.repository.reposervice.OrderMessageRepoService;
import com.pathdx.repository.reposervice.UsersRepoService;
import com.pathdx.service.CaseAllocationService;
import com.pathdx.utils.AuditLogUtil;
import com.pathdx.utils.EmailUtil;
import com.pathdx.utils.NotificationUtil;
import com.pathdx.utils.ValidationsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.pathdx.constant.Constants.BODY_Start;
import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;
import static com.pathdx.constant.Constants.UNABLE_TO_PROCESS;
import static com.pathdx.constant.Constants.WISH;
import static com.pathdx.constant.UtilConstants.*;
import static com.pathdx.constant.UtilConstants.SUBJECT;

@Component
@Slf4j
public class CaseAllocationServiceImpl implements CaseAllocationService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    CaseAllocationRepository caseAllocationRepository;

    @Autowired
    LabDetailRepository labDetailRepository;

    @Autowired
    OrderMessagesRepository orderMessagesRepository;

    @Autowired
    EmailUtil emailUtil;

    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    OrderMessageRepoService orderMessageRepoService;

    @Autowired
    AuditLogUtil auditLogUtil;

    @Autowired
    UsersRepoService usersRepoService;

    @Override
    public ResponseDto<CountAssignedAndUnAssigned> getAssignedCasesCount(String labId) {
        ResponseDto<CountAssignedAndUnAssigned> countsDto = new ResponseDto<>();
        Optional<LabDetail> labDetail = labDetailRepository.findByLabid(labId);
        //List<OrderMessages> orderMessagesList = orderMessagesRepository.findByLabDetailAndCaseStatus(labDetail.get(), CommonConstants.ASSINGED_CASES);
        Long allCount = orderMessageRepoService.allCasesCount(labDetail.get().getLabid(), CommonConstants.ASSINGED_CASES);
        Long assingedCount = orderMessageRepoService.assingedCasesCount(labDetail.get().getLabid(), CommonConstants.ASSINGED_CASES);

        /*long aCount = orderMessagesList.stream().filter(or->or.getUserModels().size()>0).count();
        long unCount = orderMessagesList.stream().filter(or->or.getUserModels().size()==0).count();*/

        CountAssignedAndUnAssigned countAssignedAndUnAssigned = new CountAssignedAndUnAssigned();
        countAssignedAndUnAssigned.setAssingedCasesCount(assingedCount);
        countAssignedAndUnAssigned.setUnAssingedCasesCount(allCount-assingedCount);
        countsDto.setResponse(countAssignedAndUnAssigned);
        countsDto.setStatusCode(HttpStatus.OK.value());
        log.info("aCount::{}",assingedCount);
        log.info("unCount::{}",allCount-assingedCount);
        return countsDto;
    }

    @Override
    public ResponseDto<List<CaseAllocationResponseDto>> getAllLabUsers(LabDetail labDetail) throws Exception {
        List<CaseAllocationResponseDto> caseAllocationArr = new ArrayList<>();
        try {
            List<UserModel> userModelListDB = usersRepository.findUserModelByLabDetails(labDetail);
            log.info("userModelListDB size:: {}",userModelListDB.size());
            List<UserModel> userModelList = new ArrayList<>();
            for (UserModel userModel : userModelListDB) {
                if (userModel.getRoles().size() > 0) {
                    if (userModel.getRoles().get(0).getRoleName()
                            .equalsIgnoreCase(CommonConstants.LAB_REVIEWER)) {
                        userModelList.add(userModel);
                    }
                }
            }

            List<CaseAllocationConfigModel> caseAllocationConfigModelList = caseAllocationRepository.findAll();
            log.info("caseAllocationConfigModelList size:: {}",caseAllocationConfigModelList.size());
            for (UserModel userModel : userModelList) {
                for (CaseAllocationConfigModel caseAllocationConfigModel : caseAllocationConfigModelList) {
                    if (userModel.getId().equals(caseAllocationConfigModel.getUserModel().getId())) {
                        CaseAllocationResponseDto temp = new CaseAllocationResponseDto();
                        temp.setFirstName(userModel.getFirstName());
                        temp.setLastName(userModel.getLastName());
                        temp.setEmail(userModel.getEmail());
                        if (userModel.getRoles().size() > 0)
                            temp.setRole(userModel.getRoles().get(0).getRoleName());
                        if (userModel.getDesignation().size() > 0)
                            temp.setDesignation(userModel.getDesignation().get(0).getDesignationName());
                        temp.setUser_id(userModel.getId());
                        temp.setMaxNumberOfCases(caseAllocationConfigModel.getMaxNumberOfCases());
                        temp.setMaxPendingDays(caseAllocationConfigModel.getMaxPendingDays());
                        caseAllocationArr.add(temp);
                    }
                }
            }
            log.info("caseAllocationArr size:: {}",caseAllocationArr.size());
        }catch (Exception e){
            log.info("Exception occurred in getAllLabUsers:: {}",e);
            log.info(e.getMessage());
        }

        if(caseAllocationArr.size()>0)
            return new ResponseDto(caseAllocationArr, "", SUCCESS_MESSAGE, HttpStatus.OK.value());
        else
            return new ResponseDto(caseAllocationArr, "", SUCCESS_MESSAGE, HttpStatus.NO_CONTENT.value());
    }

    @Override
    public ResponseDto updateUserConfig(UserConfigReqDto userConfigReqDto,String email) throws Exception{
        CaseAllocationConfigModel caseAllocationConfigModel = new CaseAllocationConfigModel();
        ResponseDto response = new ResponseDto<>();
        //setting user model to get allocation id
        UserModel userModel = new UserModel();
        userModel.setId(userConfigReqDto.getUser_id());
        Optional<CaseAllocationConfigModel> optionalCaseAllocationConfigModel  = caseAllocationRepository.findByUserModel(userModel);
        String userEmail = "";
        if(optionalCaseAllocationConfigModel.isPresent()){
            //setting callAllocationConfigModal to update
            caseAllocationConfigModel.setId(optionalCaseAllocationConfigModel.get().getId());
            caseAllocationConfigModel.setMaxNumberOfCases(userConfigReqDto.getMaxNumberOfCases());
            caseAllocationConfigModel.setMaxPendingDays(userConfigReqDto.getMaxPendingDays());
            caseAllocationConfigModel.setUserModel(optionalCaseAllocationConfigModel.get().getUserModel());
            userEmail = caseAllocationConfigModel.getUserModel().getEmail();
            //updating max pending days and max cases allocation for one user
            caseAllocationRepository.save(caseAllocationConfigModel);
            log.info("user settings updated");
            //response.setResponse(caseAllocationConfigModel1);
            response.setStatusCode(HttpStatus.OK.value());
            response.setSuccessMsg(SUCCESS_MESSAGE);

            try {
                Optional<UserModel> userModel1 = usersRepository.findUserModelByEmail(email);
                if(userModel1.isPresent()) {
                    //for Audit log
                    Optional<LabDetail> labDetail = labDetailRepository.findByLabid(userConfigReqDto.getLabId());
                    if(labDetail.isPresent()) {
                        Object[] args = {userEmail, labDetail.get().getLabName()};
                        ActionModel actionModel = auditLogUtil.getActions(AuditAction.SETTING_ALLOCATION);
                        String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(), args);
                        log.info("SETTING_ALLOCATION message {}", msg);
                        auditLogUtil.saveAuditLogs(AuditAction.SETTING_ALLOCATION, userModel1.get().getId(),
                                null, userConfigReqDto.getLabId(), null, null, msg);
                    }
                }
            }catch (Exception e){
                log.info("Exception occured while saving into audit logs::{}",e);
            }

        }else{
            response.setSuccessMsg(UNABLE_TO_PROCESS);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
        }
        return response;
    }

    @Override
    public ResponseDto updateBulkUserConfig(List<UserConfigReqDto> userConfigReqDtoList) throws Exception {
        ResponseDto response = new ResponseDto<>();
        //converting requestdto to required usermodel
        List<UserModel> userModelList=userConfigReqDtoList.stream()
                .map(userConfigReqDto -> DtoToUserModal(userConfigReqDto))
                .collect(Collectors.toList());

        //loading all CaseAllocationConfigModel ids to update
        List<CaseAllocationConfigModel> mainList  = caseAllocationRepository.findAllByUserModelIn(userModelList);
        log.info("users size::"+mainList.size());
        int maxxNumberOfDays=0;int maxPendingDays=0;
        if(userConfigReqDtoList.size()>0) {
            maxxNumberOfDays = userConfigReqDtoList.get(0).getMaxNumberOfCases();
            maxPendingDays = userConfigReqDtoList.get(0).getMaxPendingDays();
        }
        for(CaseAllocationConfigModel cam:mainList){
            if(userConfigReqDtoList.size()>0) {
                cam.setMaxNumberOfCases(maxxNumberOfDays);
                cam.setMaxPendingDays(maxPendingDays);
            }
        }

        if(mainList.size()>0){
            //updating max pending days and max cases allocation for all users
            List<CaseAllocationConfigModel> caseAllocationConfigModelList = caseAllocationRepository.saveAll(mainList);
            log.info("All users settings updated");
            response.setStatusCode(HttpStatus.OK.value());
            response.setSuccessMsg(SUCCESS_MESSAGE);
        }else{
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            response.setSuccessMsg(SUCCESS_MESSAGE);
        }
        return response;
    }


    public void casesAllocationByCri() {

        //orderMessageRepoService.getAssignedCasesCount();

    }

    public void casesAllocation() {
        //casesAllocationByCri();
        try {
            //Load all labs
            List<LabDetail> labDetails = labDetailRepository.findAll();
            for (LabDetail lab : labDetails) {
                //Load all assigned cases with count
                log.info("--lab id--{}",lab.getLabid());
                checkAndAllocationRec(lab);
            }
        }catch(Exception e){
            log.info("Exception occurred in casesAllocation");
            log.error(e.getMessage());
        }
    }

    private void checkAndAllocationRec(LabDetail lab) {
        try {

            List<AllocationAssignedCasesDto> aaDtoMain =
                    orderMessageRepoService.getAssignedCasesCount(lab.getLabid());
            aaDtoMain.stream().forEach(aa -> log.info("getAssignedCasesCount list {}", aa.getUserId()));
            List<CaseAllocationConfigModel> cacmAll = caseAllocationRepository.findAll();
            cacmAll = cacmAll.stream().filter(cm->cm.getMaxNumberOfCases()!=0).collect(Collectors.toList());
            cacmAll.stream().forEach(aa -> log.info("cacmAll before roles size list {}", aa.getId()));
            cacmAll = cacmAll.stream().filter(cm->cm.getUserModel().getRoles().size()>0).collect(Collectors.toList());
            cacmAll.stream().forEach(aa -> log.info("cacmAll before role name list {}", aa.getId()));
            cacmAll = cacmAll.stream().filter(cm->cm.getUserModel().getRoles().get(0).getRoleName().equalsIgnoreCase("Lab Reviewer")).collect(Collectors.toList());
            cacmAll.stream().forEach(aa -> log.info("cacmAll after role name list {}", aa.getId()));

            cacmAll = cacmAll.stream().filter(aa->aa.getUserModel().isActive()).collect(Collectors.toList());
            cacmAll.stream().forEach(aa -> log.info("cacmAll after active check list {}", aa.getId()));

            List<AllocationAssignedCasesDto> aaDto = new ArrayList<>();

            for(AllocationAssignedCasesDto alloAssignedCasesDto:aaDtoMain){
                UserModel userModel = usersRepoService.findRoles(alloAssignedCasesDto.getUserId());
                boolean existFlag = false;
                for(Role role:userModel.getRoles()){
                    if(role.getRoleName().equalsIgnoreCase(CommonConstants.LAB_REVIEWER)){
                        existFlag = true;
                    }
                }
                if(existFlag) {
                    Optional<CaseAllocationConfigModel> optionalCas = caseAllocationRepository.findByUserModel(userModel);
                    if (optionalCas.isPresent()) {
                        alloAssignedCasesDto.setMaxPendingDays(optionalCas.get().getMaxPendingDays());
                        alloAssignedCasesDto.setMaxNumberOfCases(optionalCas.get().getMaxNumberOfCases());
                        alloAssignedCasesDto.setCaseAllocationId(optionalCas.get().getId());
                        if(optionalCas.get().getMaxNumberOfCases()>0) {
                            aaDto.add(alloAssignedCasesDto);
                        }
                    }
                }
            }

            for(CaseAllocationConfigModel cacm:cacmAll){
                boolean existFlag = false;
                for(AllocationAssignedCasesDto alloAssignedCasesDto:aaDto){
                    if(alloAssignedCasesDto.getUserId().equals(cacm.getUserModel().getId())){
                        existFlag = true;
                    }
                }
                if(!existFlag) {
                    UserModel userModel = usersRepoService.findLabDetails(cacm.getUserModel().getId());
                    //Optional<UserModel> userModel = usersRepository.findUserModelByEmail(cacm.getUserModel().getEmail());
                    boolean labFlag = false;
                    for (LabDetail lab1 : userModel.getLabDetails()) {
                        if (lab1.getLabid().equalsIgnoreCase(lab.getLabid())) {
                            labFlag = true;
                        }
                    }
                    if (labFlag) {
                        AllocationAssignedCasesDto a = new AllocationAssignedCasesDto();
                        a.setMaxPendingDays(cacm.getMaxPendingDays());
                        a.setMaxNumberOfCases(cacm.getMaxNumberOfCases());
                        a.setAssingedCasesCount(0);
                        a.setCaseAllocationId(cacm.getId());
                        a.setUserId(cacm.getUserModel().getId());
                        aaDto.add(a);
                    }
                }
            }

            log.info("-----before sortig----");
            aaDto.stream().forEach(aa -> log.info("userId {}", aa.getUserId()));
            log.info("-----after sortig----");
            aaDto.sort((aacdto1, aacdto2) -> {
                int r1 = Math.round(aacdto1.getAssingedCasesCount() * 10000 / aacdto1.getMaxNumberOfCases()) / 100;
                int r2 = Math.round(aacdto2.getAssingedCasesCount() * 10000 / aacdto2.getMaxNumberOfCases()) / 100;
                //int l = (aacdto1.getAssingedCasesCount() / aacdto1.getMaxNumberOfCases()) - (aacdto2.getAssingedCasesCount() / aacdto2.getMaxNumberOfCases());
                return r1 - r2;
            });
            aaDto.stream().forEach(aa -> log.info("userId {}", aa.getUserId()));

            if (aaDto.size() > 0) {
                int l= Math.round(aaDto.get(0).getAssingedCasesCount() * 10000 / aaDto.get(0).getMaxNumberOfCases()) / 100;

                log.info("first user percentage {}", l);
                if (l >= 100) {
                    log.info("Reached max cases limit for all uses");
                } else {
                    //Load all unassigned cases
                    List<AllocationUnassignedDto> uassresponseDto =
                            orderMessageRepoService.findUnAssignedCasesAllocation(lab.getLabid());
                    log.info("uaresponseDto {}", uassresponseDto);
                    log.info("size {}", uassresponseDto);
                    if (uassresponseDto.size() > 0) {
                        log.info("unassigned cases found");
                        Long orderMessageID = uassresponseDto.get(0).getOrderMessageId();
                        Long userId = aaDto.get(0).getUserId();
                        assignCaseToUser(userId, orderMessageID,lab);
                        checkAndAllocationRec(lab);
                    }else{
                        log.info("no unassigned cases found");
                    }
                }
            }else{
                log.info("Users not found to allocate cases");
            }
        }catch (Exception e){
            log.info("Exception occurred in checkAndAllocationRec");
            log.info(e+"");
        }
    }

    public void assignCaseToUser(Long userId,Long orderMessageId,LabDetail lab){
        try {
            UserModel userModel = usersRepository.findById(userId).get();
            Set<UserModel> userModels = new HashSet<>();
            userModels.add(userModel);
            OrderMessages orderMessages = orderMessagesRepository.findById(orderMessageId).get();
            //orderMessages.setAssignedBy(email);
            orderMessages.setAssignedDate(new Date(System.currentTimeMillis()));
            orderMessages.setUserModels(userModels);
            orderMessages.setAssignedDate(new Date(System.currentTimeMillis()));
            OrderMessages orderMessages1 = orderMessagesRepository.save(orderMessages);
            log.info("OrderMessageId {}",orderMessages.getAccessionId());
            log.info("assigned user {}",userModel.getEmail());

            //for user notification
            try{
                notificationUtil.saveUserNotification(ActionType.CASE_ASSIGNED_BY_RULE, orderMessages1.getAccessionId(), userModel.getEmail(),lab.getLabName());
            }catch (Exception e){
                log.info("Exception occured while saving notification::{}",e);
            }

            try {
                //for Audit log
                ActionModel actionModel = auditLogUtil.getActions(AuditAction.CASE_ASSIGNED_BY_RULE);
                Object[] args = {orderMessages1.getAccessionId()};
                String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(), args);
                log.info("Action Type {}",AuditAction.CASE_ASSIGNED_BY_RULE);
                log.info("userModel.getId() {}",userModel.getId());
                log.info("lab.getLabid() {}",lab.getLabid());
                log.info("orderMessageId {}",orderMessageId);
                log.info("msg {}",msg);
                auditLogUtil.saveAuditLogs(AuditAction.CASE_ASSIGNED_BY_RULE, userModel.getId(),
                        null, lab.getLabid(),orderMessageId, null, msg);
            }catch (Exception e){
                log.info("Exception occured while saving into audit logs::{}",e);
            }

        }catch (Exception e){
            log.info("Exception occurred in assignCaseToUser");
            log.error(e.getMessage());
        }
    }

    public UserModel DtoToUserModal(UserConfigReqDto userConfigReqDto){
        UserModel userModel = new UserModel();
        userModel.setId(userConfigReqDto.getUser_id());
        return userModel;
    }
    public AllocationAssignedCasesDto convertAssignedResponseDto(AllocationAssignedCasesOpenDto allunOpenDto){
        AllocationAssignedCasesDto temp = new AllocationAssignedCasesDto();
        temp.setMaxNumberOfCases(allunOpenDto.getMaxNumberOfCases());
        temp.setMaxPendingDays(allunOpenDto.getMaxPendingDays());
        temp.setAssingedCasesCount(allunOpenDto.getAssingedCasesCount());
        temp.setUserId(allunOpenDto.getUserId());
        temp.setCaseAllocationId(allunOpenDto.getCaseAllocationId());
        return temp;
    }
    public AllocationUnassignedDto convertUnAssignedResponseDto(AllocationUnassignedOpenDto allunOpenDto){
        AllocationUnassignedDto temp = new AllocationUnassignedDto();
        temp.setAccessionId(allunOpenDto.getAccessionId());
        temp.setOrderMessageId(allunOpenDto.getOrderMessageId());
        return temp;
    }


    public void sendEmialForPendingCases()  {
        try {
            Map<String, String> mapMailContent = new HashMap<>();

            //Loading all labs
            List<LabDetail> labList = labDetailRepository.findAll();
            for (LabDetail labDetails : labList) {
                //Loading each case which is open state
                log.info("lab id::{}",labDetails.getLabid());
                //List<OrderMessages> orderMessagesList = orderMessagesRepository.findByLabDetailAndCaseStatus(labDetails, CommonConstants.ASSINGED_CASES);
                List<OrderMessages> orderMessagesList = orderMessageRepoService.findOrderMessageByLabDetailAndCaseStatus(labDetails.getLabid(), CommonConstants.ASSINGED_CASES);
                log.info("Size and List of assigned cases::{}",orderMessagesList.size());
                orderMessagesList.stream().forEach(aa -> log.info("Accession Id {}", aa.getAccessionId()));
                for (OrderMessages orderMessages : orderMessagesList) {
                    Set<UserModel> userModelSet = orderMessages.getUserModels();
                    log.info("userModelSet size ::{}",userModelSet.size());
                    List<CaseAllocationConfigModel> caseAllocationConfigModel = new ArrayList<CaseAllocationConfigModel>();
                    //One messageId may multiple users
                    userModelSet.stream().forEach(userModel -> {
                                //caseAllocationConfigModel.add((caseAllocationRepository.findByUserModel(userModel)).get());
                                try{
                                    Optional<CaseAllocationConfigModel> local =
                                            caseAllocationRepository.findByUserModel(userModel);
                                    if (local.isPresent()) {
                                        caseAllocationConfigModel.add(local.get());
                                    }
                                }catch (Exception e){
                                    log.info("Exception while streaming userModelSet::"+e);
                                }

                            }
                    );
                    log.info("accession Id checking for sendEmialForPendingCases:: "+orderMessages.getAccessionId());
                    try {
                        caseAllocationConfigModel.stream().forEach(caseAllModal -> {
                            try {
                                Date adate = orderMessages.getAssignedDate();
                                int maxPendingDays = caseAllModal.getMaxPendingDays();
                                LocalDate localDate = adate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                LocalDate maxDays = localDate.plusDays(maxPendingDays);
                                LocalDate today = LocalDate.now();
                                Period period = Period.between(maxDays, today);

                                log.info("date:{}", adate);
                                log.info("period:{}", period.getDays());
                                //Code to prepare mail message body for max pending days reached
                                if (period.getDays() > 0) {
                                    Optional<UserModel> userModel = usersRepository.findById(caseAllModal.getUserModel().getId());
                                    if (userModel.isPresent()) {
                                        UserModel user = userModel.get();
                                        String message = "";
                                        if (mapMailContent.containsKey(user.getEmail())) {
                                            message = mapMailContent.get(user.getEmail());
                                            message = message + "<br> The case " + orderMessages.getAccessionId() + " is pending from " + period.getDays()+1 + " days";
                                        } else {
                                            message = "The case " + orderMessages.getAccessionId() + " is pending from " + period.getDays()+1 + " days";
                                        }
                                        mapMailContent.put(user.getEmail(), message);
                                    }
                                }
                            }catch (Exception e){
                                log.info("Exception occurred while sending mail for accession Id::{}",orderMessages.getAccessionId());
                            }
                        });
                    }catch (Exception e){
                        log.info("Exception occurred while sending mail for accession Id::{}",orderMessages.getAccessionId());
                        log.info("{}",e);
                    }

                }
            }
            //Mail sedning code for each user in map
            mapMailContent.forEach((k, v) -> {
                sendMail(k, v);
            });
        }catch (Exception e){
            log.info("Exception occurred in sendEmialForPendingCases");
            log.info(e.getMessage());
            log.info(e+"");
        }
    }


    private void sendMail(String keyEmail, String valueMessageBody) {
        Optional<UserModel> userModel = usersRepository.findUserModelByEmail(keyEmail);
        String msgBody=WISH+userModel.get().getFirstName()+"<br><br>"+valueMessageBody+"<br><br>"+THANK;
        EmailModelDto emailModelDto=new EmailModelDto();
        emailModelDto.setFrom("info@pathforcetech.com");
        emailModelDto.setTo(keyEmail);
        emailModelDto.setSubject(PENDING_CASES_SUBJECT);
        emailModelDto.setBody(msgBody);
        try {
            emailUtil.sendmail(emailModelDto);
            log.info("Pending cases info mail sent successfully to {}",keyEmail);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
