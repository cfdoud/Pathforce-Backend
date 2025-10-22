package com.pathdx.service.impl;

import com.pathdx.constant.AuditAction;
import com.pathdx.constant.CommonConstants;
import com.pathdx.dto.requestDto.LabDetailDto;
import com.pathdx.dto.requestDto.LabDetailsRequestDto;
import com.pathdx.dto.requestDto.UserModelReqDto;
import com.pathdx.dto.responseDto.LabResponseDto;
import com.pathdx.dto.responseDto.MasterResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.ActionModel;
import com.pathdx.model.LabDetail;
import com.pathdx.model.Role;
import com.pathdx.model.UserModel;
import com.pathdx.repository.LabDetailRepository;
import com.pathdx.repository.UsersRepository;
import com.pathdx.service.LabDetailService;
import com.pathdx.utils.AuditLogUtil;
import com.pathdx.utils.GoogleCloudStorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;

@Component
@Slf4j
public class LabDetailServiceImpl implements LabDetailService {

    @Autowired
    LabDetailRepository labDetailRepository;

    @Autowired
    ModelMapper mapper;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    GoogleCloudStorageUtil googleCloudStorageUtil;

    @Autowired
    AuditLogUtil auditLogUtil;

    @Value("${caseImageBucket}")
    private String caseImageBucket;

    @Value("${bucketName}")
    private String bucketName;

    @Override
    public ResponseDto<List<MasterResponseDto>> getAllLabs() throws Exception {
        List<MasterResponseDto> responseDto = new ArrayList<>();
        List<LabDetail> labDetails = labDetailRepository.findAll();

        for (LabDetail detail:labDetails) {
            MasterResponseDto labResponseDto = new MasterResponseDto();
            labResponseDto.setLabId(detail.getLabid());
            labResponseDto.setName(detail.getLabName());
            responseDto.add(labResponseDto);
        }

        if(responseDto.size()>0)
            return new ResponseDto(responseDto, "", SUCCESS_MESSAGE, HttpStatus.OK.value());
        else
            return new ResponseDto(responseDto, "", SUCCESS_MESSAGE, HttpStatus.NO_CONTENT.value());
    }

//    @Override
//    public ResponseDto<List<MasterResponseDto>> getAssociatedLabs(String email){
//        List<MasterResponseDto> responseDto = new ArrayList<>();
//        List<LabDetail> labDetails = labDetailRepository.getAssociatedLabsByUser(email);
//    }

    @Override
    public ResponseDto<List<LabResponseDto>> getAllLabsDetails() throws Exception {
        List<LabDetail> labDetails = labDetailRepository.findAll();
        List<LabResponseDto> labResponseDto = labDetails.stream()
                .map(labResDto->convertToResponseDto(labResDto))
                .collect(Collectors.toList());

        if(labResponseDto.size()>0)
            return new ResponseDto(labResponseDto, "", SUCCESS_MESSAGE, HttpStatus.OK.value());
        else
            return new ResponseDto(labResponseDto, "", SUCCESS_MESSAGE, HttpStatus.NO_CONTENT.value());
    }

    public LabResponseDto convertToResponseDto(LabDetail labDetail){
        LabResponseDto labResponseDto = new LabResponseDto();
        labResponseDto.setLabId(labDetail.getLabid());
        labResponseDto.setLabName(labDetail.getLabName());
        labResponseDto.setLabEmail(labDetail.getLabEmail());
        labResponseDto.setLabContactNo(labDetail.getLabContactNo());
        labResponseDto.setLabWebsite(labDetail.getLabWebsite());
        labResponseDto.setLabRegistrationDocument(labDetail.getLabRegistrationDocument());
        labResponseDto.setLabRegistrationNo(labDetail.getLabRegistrationNo());

        labResponseDto.setDateCreated(labDetail.getCreatedDate());
        labResponseDto.setCreatedBy(labDetail.getCreatedBy());
        labResponseDto.setLastModifiedDate(labDetail.getLastModifiedDate());
        labResponseDto.setLastModifiedBy(labDetail.getLastModifiedBy());

        labResponseDto.setUserName(labDetail.getUserName());

        labResponseDto.setStreet(labDetail.getStreet());
        labResponseDto.setCity(labDetail.getCity());
        labResponseDto.setState(labDetail.getState());
        labResponseDto.setZip(labDetail.getZip());

        return labResponseDto;
    };

    /*public LabDetail convertToModal(LabDetailsRequestDto labReqDto){
        LabDetail labDetail = new LabDetail();
        labDetail.setLabid(labReqDto.getLabId());
        labDetail.setLabName(labReqDto.getLabName());
        labDetail.setLabEmail(labReqDto.getLabEmail());
        labDetail.setLabContactNo(labReqDto.getLabContactNo());
        labDetail.setLabWebsite(labReqDto.getLabWebsite());
        labDetail.setLabRegistrationDocument(labReqDto.getLabRegistrationDocument());
        labDetail.setLabRegistrationNo(labReqDto.getLabRegistrationNo());

        labDetail.setCreatedDate(labReqDto.getDateCreated());
        labDetail.setCreatedBy(labReqDto.getCreatedBy());
        labDetail.setLastModifiedDate(labReqDto.getLastModifiedDate());
        labDetail.setLastModifiedBy(labReqDto.getLastModifiedBy());

        labDetail.setUserName(labReqDto.getUserName());
        return labDetail;
    };*/
    @Override
    public ResponseDto<LabResponseDto> getLabDetails(String id) {
        Optional<LabDetail> labDetail = labDetailRepository.findByLabid(id);
        LabResponseDto labResponseDto = new LabResponseDto();
        if(labDetail.isPresent()){
            labResponseDto = convertToResponseDto(labDetail.get());
            //String bucketName = bucketName;
            String objectName = id+"/lab_docs/docs/registrationdoc.pdf";
            URL signedUrl = googleCloudStorageUtil.generateSignedUrl(bucketName,objectName);
            labResponseDto.setRegistrationDocSignedUrl(signedUrl);
            objectName = id+"/lab_docs/logo/logo.png";
            signedUrl = googleCloudStorageUtil.generateSignedUrl(bucketName,objectName);
            labResponseDto.setLogoSignedUrl(signedUrl);
            return new ResponseDto(labResponseDto, "", SUCCESS_MESSAGE, HttpStatus.OK.value());
        }else{
            return new ResponseDto(labResponseDto, "", SUCCESS_MESSAGE, HttpStatus.NO_CONTENT.value());
        }
    }

    @Override
    public ResponseDto<List<MasterResponseDto>> getAssociatedlabs(String usermail) {
        List<MasterResponseDto> responseDto = new ArrayList<>();
        List<LabDetail> labDetails = new ArrayList<>();
        List<String> rolesAssociated = getRole(usermail);
        if(rolesAssociated.contains(CommonConstants.SUPER_ADMIN))
            labDetails = labDetailRepository.findAll();
        else if ((rolesAssociated.contains(CommonConstants.LAB_ADMIN) || rolesAssociated.contains(CommonConstants.LAB_REVIEWER))) {
            Optional<UserModel> userModel = usersRepository.findUserModelByEmail(usermail);
           if(userModel.isPresent())
               labDetails = userModel.get().getLabDetails();
        }

        for (LabDetail detail:labDetails) {
            MasterResponseDto labResponseDto = new MasterResponseDto();
            labResponseDto.setLabId(detail.getLabid());
            labResponseDto.setName(detail.getLabName());
            responseDto.add(labResponseDto);
        }
        if(responseDto.size()>0)
            return new ResponseDto(responseDto, "", SUCCESS_MESSAGE, HttpStatus.OK.value());
        else
            return new ResponseDto(responseDto, "", SUCCESS_MESSAGE, HttpStatus.NO_CONTENT.value());

    }

    @Override
    public ResponseDto<LabResponseDto> saevLabDetails(LabDetailsRequestDto labDetailsRequestDto,String email) throws Exception{
        ResponseDto<LabResponseDto> response = new ResponseDto<>();
        Optional<LabDetail> labDetailOptional = labDetailRepository.findByLabid(labDetailsRequestDto.getLabId());
        if(labDetailOptional.isPresent()){
            LabDetail labDetail = labDetailOptional.get();

            labDetail.setLabName(labDetailsRequestDto.getLabName());
            labDetail.setLabEmail(labDetailsRequestDto.getLabEmail());
            labDetail.setLabContactNo(labDetailsRequestDto.getLabContactNo());
            labDetail.setLabWebsite(labDetailsRequestDto.getLabWebsite());
            if(labDetailsRequestDto.getLabRegistrationDocument()!=null){
                labDetail.setLabRegistrationDocument(labDetailsRequestDto.getLabRegistrationDocument());
            }

            labDetail.setLabRegistrationNo(labDetailsRequestDto.getLabRegistrationNo());

            //labDetail.setCreatedDate(labDetailsRequestDto.getDateCreated());
            //labDetail.setCreatedBy(email);
            labDetail.setLastModifiedBy(labDetailsRequestDto.getLastModifiedBy());
            labDetail.setUserName(labDetailsRequestDto.getUserName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dateObj = null;
            try {
                dateObj = sdf.parse(sdf.format(new Date()));
            } catch (java.text.ParseException e) {
                log.info(e.getMessage());
            }
            labDetail.setLastModifiedDate(dateObj);
            labDetail.setStreet(labDetailsRequestDto.getStreet());
            labDetail.setCity(labDetailsRequestDto.getCity());
            labDetail.setState(labDetailsRequestDto.getState());
            labDetail.setZip(labDetailsRequestDto.getZip());

            LabDetail labDetail1 = labDetailRepository.save(labDetail);
            LabResponseDto labResponseDto = convertToResponseDto(labDetail1);
            response.setResponse(labResponseDto);
            response.setStatusCode(HttpStatus.OK.value());

            try{
                //for Audit log
                Optional<UserModel> userModel = usersRepository.findUserModelByEmail(email);
                if(userModel.isPresent()) {
                    ActionModel actionModel = auditLogUtil.getActions(AuditAction.SETTING_LAB_DETAILS_EDIT);
                    Object[] args = {email, labDetail.getLabName()};
                    String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(), args);
                    auditLogUtil.saveAuditLogs(AuditAction.SETTING_LAB_DETAILS_EDIT, userModel.get().getId(),
                            null, labDetail.getLabid(),null, null, msg);
                }
            }catch (Exception e){
                log.info("Error occurred while saving logs::{}",e);
            }

        }else{
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
        }
        return response;
    }

    private List<String> getRole(String email) {
        return usersRepository.findUserModelByEmail(email)
                .get()
                .getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();


    }
}
