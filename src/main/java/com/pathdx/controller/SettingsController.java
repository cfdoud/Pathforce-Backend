package com.pathdx.controller;

import com.pathdx.dto.requestDto.UpdateUserModelDto;
import com.pathdx.dto.requestDto.UserModelReqDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.UserModelResponseDto;
import com.pathdx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    UserService userService;

    @GetMapping("/labprofessionals")
    public ResponseEntity<ResponseDto> getAllProfessionalsByDesignation(@RequestParam(value = "designation") String designation){

//       System.out.println(designation);
        ResponseDto<List<UserModelResponseDto>> users = userService.getAllUsersByDesignation(designation);
        return ResponseEntity.ok(users);

//        return null;

    }


}


