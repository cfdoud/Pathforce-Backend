package com.pathdx.service.impl;

import com.pathdx.constant.AuditLogSort;
import com.pathdx.dto.requestDto.LabDetailDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.*;
import com.pathdx.repository.AuditLogsRepository;
import com.pathdx.repository.LabDetailRepository;
import com.pathdx.repository.UsersRepository;
import com.pathdx.repository.reposervice.AuditLogRepoService;
import com.pathdx.service.AuditLogsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuditLogsServiceImpl implements AuditLogsService {

    @Autowired
    private LabDetailRepository labDetailRepository;

    @Autowired
    AuditLogsRepository auditLogsRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private AuditLogRepoService auditLogRepoService;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public ResponseDto<List<LabDetailDto>> getAllLabs() {
        ResponseDto<List<LabDetailDto>> responseDto = new ResponseDto<>();
        List<LabDetail> labDetails = labDetailRepository.findAll();
        List<LabDetailDto> labDetailDtos = convertLabModeltoDto(labDetails);
        responseDto.setResponse(labDetailDtos);
        return responseDto;
    }
    private List<LabDetailDto> convertLabModeltoDto(List<LabDetail> lstLabDetails) {
        List<LabDetailDto> labDetailDtos = new ArrayList<LabDetailDto>();
        for(LabDetail labDetail : lstLabDetails) {
            LabDetailDto labDetailDto = new LabDetailDto();
            labDetailDto.setLabId(labDetail.getLabid());
            labDetailDto.setLabName(labDetail.getLabName());
            labDetailDtos.add(labDetailDto);
        }
        return labDetailDtos;
    }

    public AuditLogResponseDto getAllAuditLogs(String labId, Optional<Long> userId, Date fromDate, Date toDate, int firstRow, int maxRow) throws ParseException {
        AuditLogResponseDto auditLogResponseDto = new AuditLogResponseDto();
        List<AuditLogDto> auditLogDtos = new ArrayList<>();
        Pageable pageable =  PageRequest.of(firstRow, maxRow);
        Page<AuditLogRespDto> repoDtos = null;
        List<Long> userIds = new ArrayList<>();
        if(userId.isPresent()) {
            userIds.add(userId.get());
        } else {
            LabDetail labDetail = new LabDetail();
            labDetail.setLabid(labId);
            List<UserModel> userModels = usersRepository.findByLabDetails(labDetail);
            userIds = userModels.stream().map(i->i.getId()).collect(Collectors.toList());
        }

        repoDtos = auditLogRepoService.getAuditLogByFilter(labId, userIds, fromDate, toDate, pageable);

        if(repoDtos.hasContent()){
            auditLogDtos  = repoDtos.get()
                    .map((AuditLogRespDto dto) -> mapperDto(dto))
                    .toList();
            auditLogResponseDto.setTotalCount(repoDtos.getTotalElements());
            auditLogResponseDto.setTotalNoOfPages(repoDtos.getTotalPages());
            auditLogResponseDto.setAuditLogDtos(auditLogDtos);
        }
        return auditLogResponseDto;
    }


    public List<AuditLogRespDto> getAllAuditLogs(String labId, Optional<Long> userId, Date fromDate, Date toDate) throws ParseException {
        AuditLogRespDto auditLogResponseDto = new AuditLogRespDto();
        List<AuditLogRespDto> auditLogDtos = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        if(userId.isPresent()) {
            userIds.add(userId.get());
        } else {
            LabDetail labDetail = new LabDetail();
            labDetail.setLabid(labId);
            List<UserModel> userModels = usersRepository.findByLabDetails(labDetail);
            userIds = userModels.stream().map(i->i.getId()).collect(Collectors.toList());
        }
        auditLogDtos=auditLogRepoService.getAuditLogs(labId, userIds,fromDate,toDate);
        return auditLogDtos;
    }

    @Override
    public AuditLogResponseDto getAuditLogByFilter(String labId, Optional<Long> userId, Date fromDate, Date toDate, int pageNo,
                                                   int pageSize, AuditLogSort sort, String order, Map<String, String> parameters,
                                                   Optional<String> date) throws ParseException {
        AuditLogResponseDto auditLogResponseDto = new AuditLogResponseDto();
        List<AuditLogDto> auditLogDtos = new ArrayList<>();
        Pageable pageable =  PageRequest.of(pageNo, pageSize);
        List<Long> userIds = new ArrayList<>();
        if(userId.isPresent()) {
            userIds.add(userId.get());
        } else {
            LabDetail labDetail = new LabDetail();
            labDetail.setLabid(labId);
            List<UserModel> userModels = usersRepository.findByLabDetails(labDetail);
            userIds = userModels.stream().map(i->i.getId()).collect(Collectors.toList());
        }
        Page<AuditLogRespDto> repoDtos = auditLogRepoService.getAuditLogByFilter(labId, userIds, fromDate, toDate, sort, order, parameters, pageable, date);
        if(repoDtos.hasContent()){
            auditLogDtos  = repoDtos.get()
                    .map((AuditLogRespDto dto) -> mapperDto(dto))
                    .toList();
            auditLogResponseDto.setTotalCount(repoDtos.getTotalElements());
            auditLogResponseDto.setTotalNoOfPages(repoDtos.getTotalPages());
            auditLogResponseDto.setAuditLogDtos(auditLogDtos);
        }
        return auditLogResponseDto;
    }

    AuditLogDto mapperDto(AuditLogRespDto dto){
        AuditLogDto auditLogDto = new AuditLogDto();

        auditLogDto.setAuditLogId(dto.getAuditLogId());
        auditLogDto.setAccessionId(dto.getAccessionId());
        String name = null;
        if( StringUtils.isNotBlank(dto.getFirstName()))
            name = dto.getFirstName();

        if( StringUtils.isNotBlank(dto.getLastName()))
            name = name + " " + dto.getLastName();

        auditLogDto.setName(name);
        auditLogDto.setEmailId(dto.getEmailId());
        auditLogDto.setCaseStatus(dto.getCaseStatus());
        auditLogDto.setActionType(dto.getActionType());
        auditLogDto.setDescription(dto.getDescription());
        auditLogDto.setCreatedDate(dto.getDateAndTime());

        return auditLogDto;

    }

    private List<AuditLogDto> convertAuditModelToDto(List<AuditLogModel> lstAuditLogs) {
        List<AuditLogDto> lstAuditLogDto = new ArrayList<>();
        for(AuditLogModel auditLogModel : lstAuditLogs) {
            AuditLogDto auditLogDto = new AuditLogDto();
            auditLogDto.setAuditLogId(auditLogModel.getId());
            auditLogDto.setCreatedDate(auditLogModel.getDateCreated());
            auditLogDto.setAccessionId(auditLogModel.getAccessionId());
            auditLogDto.setName(auditLogModel.getActionModel().getName());
            auditLogDto.setEmailId(auditLogModel.getUserModel().getEmail());
            if(auditLogModel.getCaseDetails()!=null) {
                auditLogDto.setCaseStatus(auditLogModel.getCaseDetails().getOrderMessages().getCaseStatus());

            }
            //auditLogDto.getActionType(auditLogModel.getActionModel().getActionType());
            auditLogDto.setDescription(auditLogModel.getDescription());
            lstAuditLogDto.add(auditLogDto);
        }
        return lstAuditLogDto;
    }

    private Long getCount1(Long userId, Date fromDate, Date toDate) throws ParseException {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = builder.createQuery(Long.class);
        Root<AuditLogModel> root = criteriaBuilderQuery.from(AuditLogModel.class);
        root.join("userModel");
        Predicate predicate = builder.conjunction();
        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            predicate = builder.and(predicate, builder.between(root.get("dateCreated"), fromDate, toDate));
        }

        criteriaBuilderQuery.where(builder.and(predicate));
        criteriaBuilderQuery.where(builder.equal(root.get("userModel").get("id"), userId));
        criteriaBuilderQuery.orderBy(builder.desc(root.get("id")));
        //TypedQuery<AuditLogRespDto> query = entityManager.createQuery(criteriaBuilderQuery);
        criteriaBuilderQuery.select(builder.count(root))
                .where(predicate);
        return entityManager.createQuery(criteriaBuilderQuery)
                .getSingleResult();

    }
}
