package com.pathdx.controller;

import com.pathdx.constant.Constants;
import com.pathdx.dto.requestDto.*;
import com.pathdx.dto.responseDto.*;
import com.pathdx.service.UserService;
import com.pathdx.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.pathdx.constant.UtilConstants.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UsersController {
    @GetMapping("/test")
    public String testDeployment() {
        return "User Deployment Successful";
    }

    @Autowired
    UserService userService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;




    @RequestMapping(value = "/save", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<ResponseDto> saveUser(@Valid @RequestBody UserModelReqDto userModelDto) {
        ResponseDto<UserModelReqDto> modelDto = userService.saveUser(userModelDto);
        return ResponseEntity.ok(modelDto);
    }


    @RequestMapping(value = "/update", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<ResponseDto> updateUser(@RequestBody UpdateUserModelDto userModelDto) {
        ResponseDto<UserModelReqDto> modelDto = userService.updateUser(userModelDto);
        return ResponseEntity.ok(modelDto);
    }

    @RequestMapping(value = "/login", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<ResponseDto> login(@Valid @RequestBody UserLoginRequestDto loginRequest) {
        ResponseDto<UserLoginResponseDto> loginData = userService.login(loginRequest);
        log.info("Generated Token:: {}", loginData.getResponse().getJwtToken());
        return ResponseEntity.ok(loginData);

    }

    @RequestMapping(value = "/saml/login", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<ResponseDto> saml(@RequestBody UserLoginRequestDto loginRequest) {
        ResponseDto<UserLoginResponseDto> loginData = userService.samlLogin(loginRequest);
        log.info("Generated Token:: {}", loginData.getResponse().getJwtToken());
        return ResponseEntity.ok(loginData);

    }

    @RequestMapping(value = "/forgot/password", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<ResponseDto> forgotPassword(String emailId) {
        ResponseDto responseDto = null;
        try {
            responseDto = userService.forgotPassword(emailId);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = responseDto.getSuccessMsg();
        if (!response.startsWith("Invalid")) {

            return ResponseEntity.ok(new ResponseDto(null, "", Constants.SUCCESS_MESSAGE, HttpStatus.OK.value()));
        }
        return ResponseEntity.badRequest().body(new ResponseDto(null, Constants.UNABLE_TO_PROCESS, Constants.SUCCESS_MESSAGE, HttpStatus.BAD_REQUEST.value()));
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<ResponseDto> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers) {
        String email = jwtTokenUtil.retrieveUserNameFromToken(headers);
        String password = jwtTokenUtil.retrievePasswordFromToken(headers);
        ResponseDto<RefreshTokenResponseDto> tokenData = userService.getRefreshToken(email, password);
        log.info("Generated Token:: {}", tokenData.getResponse().getRefreshToken());
        String response = tokenData.getResponse().getMessge();
        if (!response.startsWith("Invalid"))

            return ResponseEntity.ok(new ResponseDto(tokenData.getResponse(), "", SUCCESS_MESSAGE, HttpStatus.CREATED.value()));
        else {

            return ResponseEntity.badRequest().body(new ResponseDto(null, ERROR_MESSAGE_REFRESH_TOKEN, null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/change/password")
    public ResponseEntity<ResponseDto> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers,@Valid @RequestBody ChangePasswordDto changePasswordDto) throws MessagingException, IOException {
        String email= jwtTokenUtil.retrieveUserNameFromToken(headers);
        if(email.isBlank() || email.isEmpty()){
            return ResponseEntity.badRequest().body(new ResponseDto(null, "AUTHORIZATION header is required", null, HttpStatus.BAD_REQUEST.value()));
        }
        changePasswordDto.setEmail(email);
        ResponseDto<ChangePasswordResponseDto>response= userService.changePassword(changePasswordDto);
        String message=response.getResponse().getMessage();

        if (!message.startsWith("Invalid")&&!message.startsWith("Something")) {

            return ResponseEntity.ok(new ResponseDto(changePasswordDto, "", SUCCESS_MESSAGE_CHANGE_PASS, HttpStatus.CREATED.value()));
        } else if (message.startsWith("Something")) {
            return ResponseEntity.badRequest().body(new ResponseDto(null, ERROR_MESSAGE_CHANGE_PASS, null, HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.badRequest().body(new ResponseDto(null, ERROR_MESSAGE_CHANGE_PWD, null, HttpStatus.BAD_REQUEST.value()));
    }



    @GetMapping("/getAllUsers/{labid}")
    public ResponseEntity<Map<Long, List<RoleBasedUserDto>>> getAllUsers(@PathVariable String labid) {
        Map<Long, List<RoleBasedUserDto>> users = userService.getAllUsers(labid);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{email}")
    public ResponseEntity<ResponseDto> getUserById(@PathVariable("email") String email) {
        ResponseDto<UserModelResponseDto> user = userService.getUserById(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/useractivation/{labid}/{usermail}")
    public ResponseEntity<ResponseDto> deactivateUser(@PathVariable String labid, @PathVariable String usermail,
                                                      @RequestParam String status,@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers ){
        String email= jwtTokenUtil.retrieveUserNameFromToken(headers);
    return  ResponseEntity.ok(new ResponseDto(userService.deactivateUser(labid, usermail, status, email), "", SUCCESS_MESSAGE, HttpStatus.CREATED.value()));

    }

    @GetMapping("/userslist/{labid}")
    public ResponseEntity<ResponseDto> getUsersList(@PathVariable("labid") String labId,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers ){
        String email= jwtTokenUtil.retrieveUserNameFromToken(headers);
        return  ResponseEntity.ok(new ResponseDto(userService.getUsersList(labId, email), "", SUCCESS_MESSAGE,
                HttpStatus.CREATED.value()));
    }

    @PostMapping("/addlabtouser")
    public ResponseEntity<ResponseDto> addLabToUser(@RequestBody UserLabDto userLabDto){
        return  ResponseEntity.ok(new ResponseDto(userService.addLabTouser(userLabDto), "", SUCCESS_MESSAGE,
                HttpStatus.CREATED.value()));
    }

}
