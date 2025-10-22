package com.pathdx.controller;

import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordermessages")
@Slf4j
public class OrderMessagesController {

    @Autowired
    OrderMessageService orderMessageService;
    @GetMapping("/{labId}")
    public ResponseEntity<ResponseDto> partialGlobalSearch(@PathVariable String labId) throws Exception{
        ResponseDto<Map<String, List<String>>> responseDto = new ResponseDto<>();
        try{

            responseDto = orderMessageService.getAllAccessionIds(labId);
        }
        catch (Exception e){
            throw e;
        }
        return  ResponseEntity.ok(responseDto);
    }
}
