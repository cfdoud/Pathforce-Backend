package com.pathdx.service;

import com.pathdx.dto.requestDto.ChangePasswordDto;
import com.pathdx.dto.requestDto.UserLabDto;
import com.pathdx.dto.requestDto.UserLoginRequestDto;
import com.pathdx.dto.requestDto.UserModelReqDto;
import com.pathdx.dto.responseDto.*;

import com.pathdx.model.LabDetail;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import java.util.Map;
import java.util.Set;


@Service
public interface UserService {
    ResponseDto<UserModelReqDto> saveUser(UserModelReqDto userModelDto);

    ResponseDto<UserModelReqDto> updateUser(UserModelReqDto userModelDto);

    ResponseDto<UserLoginResponseDto> login(UserLoginRequestDto loginRequest);

    ResponseDto<RefreshTokenResponseDto> getRefreshToken(String email, String password);
     ResponseDto<ChangePasswordResponseDto> changePassword(ChangePasswordDto changePasswordDto) throws MessagingException, IOException;

    public ResponseDto forgotPassword(String emailId) throws MessagingException, IOException;



    public Map<Long, List<RoleBasedUserDto>> getAllUsers(String labId);

    public ResponseDto<UserModelResponseDto> getUserById(String email);

   /* ResponseDto<Map<String, Set<CaseDetailsDto>>> getAccessionIds(int pageNo, int pageSize, String labId, String userMail, String caseStatus) throws Exception;

    ResponseDto<Map<String, Set<CaseDetailsDto>>> getAccessionIds(int pageNo, int pageSize, String labId, String userMail) throws Exception;*/


    Set<String> getassignUsers(String labId);

 /*   ResponseDto<Map<String, Set<CaseDetailsDto>>> getAccessionId(String labId, String s);*/

    ResponseDto<List<UserModelResponseDto>> getAllUsersByDesignation(String designation);

    public List<UserModelResponseDto> getUsersByLabId(LabDetail labDetail);

    String deactivateUser(String labid, String usermail, String status, String email);

    ResponseDto<UserLoginResponseDto> samlLogin(UserLoginRequestDto loginRequest);

    List<String> getUsersList(String labid, String email);

    String addLabTouser(UserLabDto userLabDto);
}
