package com.pathdx.service.impl;

import com.pathdx.dto.requestDto.ActivityLogDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.ActivityLog;
import com.pathdx.repository.ActivityLogRepository;
import com.pathdx.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepoService;

    @Override
    public ResponseDto getActivityLog(ActivityLogDto activityLogDTO) {
        ResponseDto responseDTO = new ResponseDto(Boolean.FALSE, "Not Able To Process ",HttpStatus.UNAUTHORIZED.name());

        validateActivityLogDTO(activityLogDTO, responseDTO);

        if (!(responseDTO.getStatusCode()==200)) {
            return responseDTO;
        }

        List<ActivityLog> activityLogList = activityLogRepoService.findAll();
        if (!activityLogList.isEmpty()) {
            responseDTO.setResponse(activityLogList);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setSuccessMsg("Success");
        }else{
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setError(HttpStatus.NOT_FOUND.name());
        }
        return responseDTO;
    }

    private void validateActivityLogDTO(ActivityLogDto activityLogDTO, ResponseDto responseDTO) {

        if ((activityLogDTO.getUserId() == null || activityLogDTO.getUserId().trim().equals(""))
                                                  && activityLogDTO.getCreatedDate() == null) {
            responseDTO.setError("Invalid Input");
        } else {
            responseDTO.setStatusCode(HttpStatus.OK.value());
        }
    }
}
