package com.pathdx.service.impl;

import com.pathdx.dto.responseDto.OrderMessageDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.OrderMessages;
import com.pathdx.repository.OrderMessagesRepository;
import com.pathdx.repository.UsersRepository;
import com.pathdx.repository.reposervice.OrderMessageRepoService;
import com.pathdx.repository.reposervice.UsersRepoService;
import com.pathdx.service.OrderMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    OrderMessagesRepository orderMessagesRepository;

    @Autowired
    UsersRepoService usersRepoService;

    @Autowired
    OrderMessageRepoService orderMessageRepoService;
    @Override
    public ResponseDto<Map<String, List<String>>> getAllAccessionIds(String labId) {
        ResponseDto<Map<String, List<String>>> responseDto = new ResponseDto<>();
        Map<String, List<String>> partialSearch = new HashMap<>();

        Set<String> users = usersRepoService.getUserMailByRoles(labId);
        for(String userMail : users){

            List<String> accessionIds = orderMessageRepoService.getAccessionIdForUserMail(userMail, labId);
            partialSearch.put(userMail, accessionIds);
        }
        List<String> openCases = orderMessageRepoService.getOpenAccessoinIds(labId);
        partialSearch.put("unassigned", openCases);
        responseDto.setResponse(partialSearch);
        return responseDto;
    }

    @Override
    public ResponseDto<OrderMessageDto> getOrderMessage(Long id) {
        ResponseDto<OrderMessageDto> responseDto = new ResponseDto<>();
        Optional<OrderMessages> orderMessages = orderMessagesRepository.findById(id);
        OrderMessageDto orderMessageDto = new OrderMessageDto();
        if(orderMessages.isPresent()){
            orderMessageDto.setDateReported(String.valueOf(orderMessages.get().getDateReported()));
            responseDto.setResponse(orderMessageDto);
        }
        return responseDto;
    }
}
