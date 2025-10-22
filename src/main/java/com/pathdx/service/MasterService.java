package com.pathdx.service;

import com.pathdx.dto.responseDto.MasterResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface MasterService {


   // ResponseDto<List<MasterResponseDto>> getAllAssociatedLab();

    public ResponseDto<List<MasterResponseDto>> getAllState();

    ResponseDto<List<MasterResponseDto>> getAllDesignation();

    ResponseDto<List<MasterResponseDto>> getAllRoles();
}
