package com.pathdx.service;

import com.pathdx.dto.responseDto.OrderMessageDto;
import com.pathdx.dto.responseDto.ResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface OrderMessageService {
    ResponseDto<Map<String, List<String>>> getAllAccessionIds(String labId);
    ResponseDto<OrderMessageDto> getOrderMessage(Long id);
}
