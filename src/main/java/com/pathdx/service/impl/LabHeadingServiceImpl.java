package com.pathdx.service.impl;

import com.pathdx.constant.AuditAction;
import com.pathdx.dto.requestDto.LabHeadingsReqDto;
import com.pathdx.dto.responseDto.LabHeadingsResponseDto;
import com.pathdx.dto.responseDto.LabResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.ActionModel;
import com.pathdx.model.LabDetail;
import com.pathdx.model.LabHeadings;
import com.pathdx.model.UserModel;
import com.pathdx.repository.LabDetailRepository;
import com.pathdx.repository.LabHeadingsRepository;
import com.pathdx.repository.UsersRepository;
import com.pathdx.service.LabHeadingService;
import com.pathdx.utils.AuditLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;

@Component
@Slf4j
public class LabHeadingServiceImpl implements LabHeadingService {

    @Autowired
    LabHeadingsRepository labHeadingsRepository;

    @Autowired
    LabDetailRepository labDetailRepository;


    @Autowired
    AuditLogUtil auditLogUtil;

    @Autowired
    UsersRepository usersRepository;

    /*@Override
    public ResponseDto<LabHeadings> getLabHeadingDetails(String labId) {
        ResponseDto<LabHeadings> labHeadingResponseDto =  new ResponseDto<>();
        List<LabHeadings> labHeadings = labHeadingsRepository.findAll();
        if(labHeadings.size() > 0){
            for(LabHeadings labHeadings1 : labHeadings){
                if(labHeadings1.getLabDetail().getLabid().equals(labId)){
                    labHeadingResponseDto.setResponse(labHeadings1);
                    break;
                }
            }
        }
        return labHeadingResponseDto;
    }*/

    @Override
    public ResponseDto<LabHeadingsResponseDto> getLabHeadingDetails(String labId) throws Exception {
        ResponseDto<LabHeadingsResponseDto> response = new ResponseDto<>();
        Optional<LabDetail> labDetail = labDetailRepository.findByLabid(labId);
        if(labDetail.isPresent()) {
            Optional<LabHeadings> labHeadings = labHeadingsRepository.findByLabDetail(labDetail.get());
            if(labHeadings.isPresent()) {
                LabHeadingsResponseDto labHeadingsResponseDto = convertToResponseDto(labHeadings.get());
                log.info("labHeadings..{}", labHeadings);
                response.setResponse(labHeadingsResponseDto);
                response.setStatusCode(HttpStatus.OK.value());
                response.setSuccessMsg(SUCCESS_MESSAGE);
            }else{
                response.setSuccessMsg(SUCCESS_MESSAGE);
                response.setStatusCode(HttpStatus.NO_CONTENT.value());
            }
        }else{
            response.setSuccessMsg(SUCCESS_MESSAGE);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
        }
        return response;
    }

    @Override
    public ResponseDto<LabHeadingsResponseDto> updateLabHeadings(LabHeadingsReqDto labHeadingsReqDto) throws Exception {
        ResponseDto<LabHeadingsResponseDto> responseDto = new ResponseDto<>();
        //LabHeadings labHeadings = convertToModel(labHeadingsReqDto);
        Optional<LabHeadings> labHeadings = labHeadingsRepository.findById(labHeadingsReqDto.getId());
        String labId = null;
        if(labHeadings.isPresent()){
            LabHeadings labHeading = labHeadings.get();
            labId = labHeading.getLabDetail().getLabid();
            labHeading.setFirstHeading(labHeadingsReqDto.getFirstHeading());
            labHeading.setSecondHeading(labHeadingsReqDto.getSecondHeading());
            labHeading.setThirdHeading(labHeadingsReqDto.getThirdHeading());
            labHeading.setFourthHeading(labHeadingsReqDto.getFourthHeading());
            labHeading.setFifthHeading(labHeadingsReqDto.getFifthHeading());
            labHeading.setSixthHeading(labHeadingsReqDto.getSixthHeading());
            labHeading.setSeventhHeading(labHeadingsReqDto.getSeventhHeading());
            LabHeadings labHeadings1 = labHeadingsRepository.save(labHeading);
            LabHeadingsResponseDto labResponseDto = convertToResponseDto(labHeadings1);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setResponse(labResponseDto);
            responseDto.setSuccessMsg(SUCCESS_MESSAGE);

            try{
                Optional<UserModel> userModel = usersRepository.findUserModelByEmail(labHeadingsReqDto.getEmail());
                if(userModel.isPresent()){
                    ActionModel actionModel = auditLogUtil.getActions(AuditAction.REPORT_HEADER_MODIFIED);
                    Object[] args = {labHeadingsReqDto.getLabId(),userModel.get().getEmail()};
                    String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
                    auditLogUtil.saveAuditLogs(AuditAction.LOGIN_SUCCESS, userModel.get().getId(),
                            null,labId,null,null,msg);
                }
            }catch (Exception e){
                log.info("Exception occured for audit {}",e.getMessage());
            }

        }
        if(responseDto.getResponse()!=null){
            return responseDto;
        }else{
            responseDto.setSuccessMsg(SUCCESS_MESSAGE);
            responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return responseDto;
        }
    }


    public LabHeadingsResponseDto convertToResponseDto(LabHeadings labHeadings) throws Exception{
        LabHeadingsResponseDto labHeadingsResponseDto = new LabHeadingsResponseDto();
        labHeadingsResponseDto.setId(labHeadings.getId());
        labHeadingsResponseDto.setFirstHeading(labHeadings.getFirstHeading());
        labHeadingsResponseDto.setSecondHeading(labHeadings.getSecondHeading());
        labHeadingsResponseDto.setThirdHeading(labHeadings.getThirdHeading());
        labHeadingsResponseDto.setFourthHeading(labHeadings.getFourthHeading());
        labHeadingsResponseDto.setFifthHeading(labHeadings.getFifthHeading());
        labHeadingsResponseDto.setSixthHeading(labHeadings.getSixthHeading());
        labHeadingsResponseDto.setSeventhHeading(labHeadings.getSeventhHeading());
        return labHeadingsResponseDto;
    }
}
