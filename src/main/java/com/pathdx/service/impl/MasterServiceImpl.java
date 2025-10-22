package com.pathdx.service.impl;

import com.pathdx.dto.responseDto.MasterResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.DesignationModel;
import com.pathdx.model.Role;
import com.pathdx.model.StateModel;
import com.pathdx.repository.DesignateRepository;
import com.pathdx.repository.RoleRepository;
import com.pathdx.repository.StateRespository;
import com.pathdx.service.MasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MasterServiceImpl implements MasterService {

    @Autowired
    private DesignateRepository designateRepository;

   /* @Autowired
    private AssociatedLabRepository associatedLabRepository;
*/
    @Autowired
    private StateRespository stateRespository;

    @Autowired
    RoleRepository  roleRepository;



    public ResponseDto<List<MasterResponseDto>> getAllDesignation(){
        List<DesignationModel> models = designateRepository.findAll();
        List<MasterResponseDto> masterResponseDtos = new ArrayList<>();
        for (DesignationModel model : models){
            MasterResponseDto masterResponseDto = new MasterResponseDto();
            masterResponseDto.setId(model.getId());
            masterResponseDto.setName(model.getDesignationName());
            masterResponseDtos.add(masterResponseDto);
        }
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResponse(masterResponseDtos);
        responseDto.setStatusCode(HttpStatus.OK.value());
        responseDto.setSuccessMsg("Success");
        return responseDto;
    }

  /*  public ResponseDto<List<MasterResponseDto>> getAllAssociatedLab() {

        List<AssociatedLabModel> models = associatedLabRepository.findAll();
        List<MasterResponseDto> masterResponseDtos = new ArrayList<>();
        for (AssociatedLabModel model : models){
            MasterResponseDto masterResponseDto = new MasterResponseDto();
            masterResponseDto.setId(model.getId());
            masterResponseDto.setName(model.getAssociatedLabName());
            masterResponseDtos.add(masterResponseDto);
        }
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResponse(masterResponseDtos);
        responseDto.setStatusCode(HttpStatus.OK.value());
        responseDto.setSuccessMsg("Success");
        return responseDto;

    }*/

    @Override
    public ResponseDto<List<MasterResponseDto>> getAllState() {
        List<StateModel> models = stateRespository.findAll();
        List<MasterResponseDto> masterResponseDtos = new ArrayList<>();
        for (StateModel model : models){
            MasterResponseDto masterResponseDto = new MasterResponseDto();
            masterResponseDto.setId(model.getId());
            masterResponseDto.setName(model.getStateName());
            masterResponseDtos.add(masterResponseDto);
        }
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResponse(masterResponseDtos);
        responseDto.setStatusCode(HttpStatus.OK.value());
        responseDto.setSuccessMsg("Success");
        return responseDto;
    }


    @Override
    public ResponseDto<List<MasterResponseDto>> getAllRoles() {

        List<Role> models = roleRepository.findAll();
        List<MasterResponseDto> masterResponseDtos = new ArrayList<>();
        for (Role model : models){
            MasterResponseDto masterResponseDto = new MasterResponseDto();
            masterResponseDto.setId(model.getId());
            masterResponseDto.setName(model.getRoleName());
            masterResponseDtos.add(masterResponseDto);
        }
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResponse(masterResponseDtos);
        responseDto.setStatusCode(HttpStatus.OK.value());
        responseDto.setSuccessMsg("Success");
        return responseDto;
    }


}
