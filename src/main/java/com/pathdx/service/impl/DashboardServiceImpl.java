package com.pathdx.service.impl;

import com.pathdx.constant.ActionType;
import com.pathdx.constant.AuditAction;
import com.pathdx.constant.CommonConstants;
import com.pathdx.dto.requestDto.CaseToUserDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.*;
import com.pathdx.repository.*;
import com.pathdx.repository.reposervice.OrderMessageRepoService;
import com.pathdx.repository.reposervice.SlideDetailRepoService;
import com.pathdx.service.DashboardService;
import com.pathdx.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.pathdx.constant.CommonConstants.*;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    OrderMessagesRepository orderMessagesRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    OrderMessageRepoService orderMessageRepoService;

    @Autowired
    AuditLogUtil auditLogUtil;

    @Autowired
    SlideDetailRepoService slideDetailRepoService;

    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    LabDetailRepository labDetailRepository;


    @Override
    public String assignCaseToUser(CaseToUserDto caseToUserDto, String email) {
        try {
            UserModel userModel = usersRepository.findUserModelByEmail(caseToUserDto.getUserMail()).get();
            ValidationsUtils.labValidation(caseToUserDto.getLabId(), userModel);
            Set<UserModel> userModels = new HashSet<>();
            userModels.add(userModel);
            OrderMessages orderMessages = orderMessagesRepository.findById(caseToUserDto.getOrderMessageId()).get();
            orderMessages.setAssignedBy(email);
            orderMessages.setAssignedDate(new Date(System.currentTimeMillis()));
            orderMessages.setUserModels(userModels);
            orderMessagesRepository.save(orderMessages);

            //for user notification
            try{
                log.info("*******************"+ orderMessages.getAccessionId());
                Optional<LabDetail> labDetail = labDetailRepository.findByLabid(caseToUserDto.getLabId());
                if(labDetail.isPresent()){
                    notificationUtil.saveUserNotification(ActionType.CASE_ASSIGNED_BY_ADMIN, orderMessages.getAccessionId(), email,labDetail.get().getLabName());
                }
                //for Audit log
                ActionModel actionModel = auditLogUtil.getActions(AuditAction.CASE_ASSIGNED_BY_ADMIN);
                Object[] args = {orderMessages.getAccessionId(),orderMessages.getAssignedBy(), labDetail.get().getLabName()};
                String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(), args);
                auditLogUtil.saveAuditLogs(AuditAction.CASE_ASSIGNED_BY_ADMIN, userModel.getId(),null, caseToUserDto.getLabId(),
                        caseToUserDto.getOrderMessageId(),null, msg);

            }catch (Exception e){
                log.info("Exception occured while saving notification::{}",e);
            }

            return StringUtils.joinWith(SPACE, "Case assigned successfully to the user:", caseToUserDto.getUserMail());
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public Map<String, Long> getCaseList(String labId, String email) throws ParseException {
        // Map<String, Long> caseOnStatus = new HashMap<>();
        String role = getRole(email);

        Map<String, Long> caseCount = new HashMap<>();
        long count = 0L;
        Long id = null;
        if(StringUtils.equalsIgnoreCase(role, "Lab Reviewer")) {
            id = usersRepository.findUserModelByEmail(email).get().getId();
        }
        if(!StringUtils.equalsIgnoreCase(role, LAB_REVIEWER)){
            count = orderMessageRepoService.getCaseCountByStatus(labId, Optional.ofNullable(id), NEWCASES);
            caseCount.put(NEWCASES, count);}
        count = orderMessageRepoService.getCaseCountByStatus(labId, Optional.ofNullable(id), INPROCESS);
        caseCount.put(INPROCESS, count);
        count = orderMessageRepoService.getCaseCountByStatus(labId, Optional.ofNullable(id), PENDING);
        caseCount.put(PENDING, count);
        count = orderMessageRepoService.getCaseCountByStatus(labId, Optional.ofNullable(id), CLOSED);
        caseCount.put(CLOSED, count);

        return caseCount;

    }

    private String getRole(String email) {
        String role = usersRepository.findUserModelByEmail(email)
                .get()
                .getRoles()
                .stream()
                .map(Role::getRoleName)
                .filter(s -> StringUtils.equalsIgnoreCase(s, "Lab Admin") || StringUtils.equalsIgnoreCase(s, "Super Admin"))
                .findFirst()
                .orElse("Lab Reviewer");

        return role;
    }

    @Override
    public DashboardResponseDto getCaseByStatus(String labId, CaseStatus status, int pageNo, int pageSize, String email, DashboardSort sort, String order, Map<String, String> filter, Optional<String> date, Optional<Integer> age) throws ParseException {
        String role = getRole(email);
        DashboardResponseDto dashboardResponseDto = new DashboardResponseDto();
        List<DashboardDto> dashboardDtos;
        Long userId = usersRepository.findUserModelByEmail(email).get().getId();
        Pageable pageable =  PageRequest.of(pageNo, pageSize);
        boolean aging = false;
        boolean isScanned = false;
        if(status.equals(CaseStatus.NEWCASES) && StringUtils.equalsIgnoreCase(role, LAB_REVIEWER))
            return dashboardResponseDto;
        if(status.equals(CaseStatus.INPROCESSCASES) || status.equals(CaseStatus.PENDINGCASES))
            aging = true;
        if(status.equals(CaseStatus.NEWCASES) || status.equals(CaseStatus.INPROCESSCASES))
            isScanned = true;

        Page<RepoDto> repoDtos = orderMessageRepoService.getCaseByStatus(status, labId, userId, role, pageable, sort,
                order, filter, date, age);


        if(repoDtos.hasContent()){
            boolean finalAging = aging;
            boolean finalIsScanned = isScanned;
            dashboardDtos  = repoDtos.get()
                    .map((RepoDto dto) -> {
                        try {
                            return mapperDto(dto, finalAging, finalIsScanned);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            dashboardResponseDto.setTotalCount(repoDtos.getTotalElements());
            dashboardResponseDto.setTotalNoOfPages(repoDtos.getTotalPages());
            dashboardResponseDto.setDashboardDtoList(dashboardDtos);
        }
        return dashboardResponseDto;
    }


    @Override
    public Map<String, List<YearWiseCaseCount>> getMyActivity(String labId, String email) {
        List<YearWiseCaseCount> yearWiseCaseCounts = new ArrayList<>();

        String role = getRole(email);
        Map<String, List<YearWiseCaseCount>> myActivity = new HashMap<>();
        Long id = null;
        if(StringUtils.equalsIgnoreCase(role, LAB_REVIEWER) ) {
            id = usersRepository.findUserModelByEmail(email).get().getId();
        }
        if(StringUtils.equalsIgnoreCase(role, LAB_ADMIN) || StringUtils.equalsIgnoreCase(role, SUPER_ADMIN)){
            yearWiseCaseCounts = orderMessageRepoService.myActivity(labId, Optional.ofNullable(id), CommonConstants.NEWCASES);
            myActivity.put(NEWCASES, yearWiseCaseCounts);
        }

        yearWiseCaseCounts = orderMessageRepoService.myActivity( labId, Optional.ofNullable(id), INPROCESS);
        myActivity.put(INPROCESS, yearWiseCaseCounts);

        yearWiseCaseCounts = orderMessageRepoService.myActivity(labId, Optional.ofNullable(id),PENDING);
        myActivity.put(PENDING, yearWiseCaseCounts);

        yearWiseCaseCounts = orderMessageRepoService.myActivity( labId, Optional.ofNullable(id),CLOSED);
        myActivity.put(CLOSED, yearWiseCaseCounts);

        return myActivity;}


    DashboardDto mapperDto(RepoDto dto, boolean aging, boolean isScanned) throws ParseException {
        DashboardDto dashboardDto = new DashboardDto();

        dashboardDto.setAccessionId(dto.getAccessionId());
        dashboardDto.setOrderMessageId(dto.getOrderMessageId());
        dashboardDto.setHospitalName(dto.getHospitalName());
        dashboardDto.setDateAndTime(dto.getDateAndTime());
        String patientname = null;
        String physicianname = null;
        if( StringUtils.isNotBlank(dto.getPFName()))
            patientname = dto.getPFName();
        if( StringUtils.isNotBlank(dto.getPMName()))
            patientname = patientname + " " + dto.getPMName();

        if( StringUtils.isNotBlank(dto.getPLName()))
            patientname = patientname + " " + dto.getPLName();

        if( StringUtils.isNotBlank(dto.getPhFName()))
            physicianname = dto.getPhFName();
        if( StringUtils.isNotBlank(dto.getPhMName()))
            physicianname = physicianname + " " + dto.getPhMName();
        if( StringUtils.isNotBlank(dto.getPhLName()))
            physicianname = physicianname + " " + dto.getPhLName();
        dashboardDto.setPatientname(patientname);
        dashboardDto.setPhysicianName(physicianname);
        if(aging){
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar c = Calendar.getInstance(timeZone);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);

            c.setTime(sdf.parse(sdf.format(c.getTime())));
            // DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            long diff = (c.getTime().getTime() - sdf.parse(sdf.format(dto.getAssignedDate())).getTime());
            dashboardDto.setAging(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        }
        if(isScanned){
            Long id = dto.getOrderMessageId();
            Long counts = slideDetailRepoService.countbyOrderMessage(id);
            if(counts == 0)
                dashboardDto.setScanned(false);
            else
                dashboardDto.setScanned(true);
        }

        return dashboardDto;

    }


}
