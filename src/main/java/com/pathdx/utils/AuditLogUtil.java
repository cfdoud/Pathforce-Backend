package com.pathdx.utils;

import com.pathdx.model.ActionModel;
import com.pathdx.model.AuditLogModel;
import com.pathdx.repository.ActionRepository;
import com.pathdx.repository.AuditLogsRepository;
import com.pathdx.repository.reposervice.AuditLogRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pathdx.constant.AuditAction;
import com.pathdx.model.*;
import com.pathdx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AuditLogUtil {

    @Autowired
    private AuditLogsRepository auditLogsRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CaseDetailsRepository caseDetailsRepository;

    @Autowired
    private LabDetailRepository labDetailRepository;

    @Autowired
    private OrderMessagesRepository orderMessagesRepository;
    @Autowired
    AuditLogRepoService auditLogRepoService;

    public void saveAuditLogs(AuditAction action,  Long userId, String message) {
        AuditLogModel auditLogModel = getAuditLogModel(action, message, userId, null, null, null);
        auditLogsRepository.save(auditLogModel);
    }

    public void saveAuditLogs(AuditAction action, Long userId, Long caseId, String labId, Long orderMessageId, String emailId, String message) {
        AuditLogModel auditLogModel = getAuditLogModel(action, message, userId, caseId, labId, orderMessageId);
        auditLogsRepository.save(auditLogModel);
    }

    public void saveAuditLogs(AuditAction action, Long userId, Long caseId, String emailId, String message) {
        AuditLogModel auditLogModel = getAuditLogModel(action, message, userId, caseId, null, null);
        auditLogsRepository.save(auditLogModel);
    }

    public void saveAuditLogs(AuditAction action, Long userId, String labId, String emailId, String message) {
        AuditLogModel auditLogModel = getAuditLogModel(action, message, userId, null, labId, null);
        auditLogsRepository.save(auditLogModel);
    }

    public ActionModel getActions(AuditAction action) {
       ActionModel actionModel =  actionRepository.findByName(action.toString());
       return actionModel;
    }

    public String getMessageFormat(String message, Object[] args) {
        MessageFormat messageFormat = new MessageFormat(message);
        String result = messageFormat.format(args);
        return result;
    }

    public AuditLogModel getAuditLogModel(AuditAction action, String message, Long userId, Long caseId, String labId, Long orderMessageId) {
        AuditLogModel auditLogModel = new AuditLogModel();
        Optional<UserModel> userModel = usersRepository.findById(userId);
        if(userModel.isPresent()) {
            auditLogModel.setCreatedBy(userModel.get().getFirstName()+ " " +userModel.get().getLastName());
            auditLogModel.setUserModel(userModel.get());
        }

        if(caseId != null) {
            Optional<CaseDetails> caseDetails = caseDetailsRepository.findById(caseId);
            if(caseDetails.isPresent()) {
                auditLogModel.setAccessionId(caseDetails.get().getOrderMessages().getAccessionId());
                auditLogModel.setCaseDetails(caseDetails.get());
            }
        }

        if(orderMessageId!=null) {
            Optional<OrderMessages> orderMessages = orderMessagesRepository.findById(orderMessageId);
            if(orderMessages.isPresent()) {
                auditLogModel.setAccessionId(orderMessages.get().getAccessionId());
            }
        }

        if(labId!=null) {
            Optional<LabDetail> labDetail = auditLogRepoService.getLabDetails(labId);
            if(labDetail.isPresent()) {
                auditLogModel.setLabDetail(labDetail.get());
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObj = null;
        try {
            dateObj = sdf.parse(sdf.format(new Date()));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        auditLogModel.setDescription(message);
        auditLogModel.setDateCreated(dateObj);
        ActionModel actionModel = new ActionModel();
        actionModel = getActions(action);
        auditLogModel.setActionModel(actionModel);
        return auditLogModel;
    }

}
