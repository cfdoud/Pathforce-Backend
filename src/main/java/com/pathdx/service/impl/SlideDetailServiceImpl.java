package com.pathdx.service.impl;

import com.pathdx.dto.requestDto.CaseDetailsDto;
import com.pathdx.dto.requestDto.SlideDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.SVSDataResponseDTO;
import com.pathdx.dto.responseDto.SlideDetailsDto;
import com.pathdx.dto.responseDto.SlideResDto;
import com.pathdx.model.CaseDetails;
import com.pathdx.model.CaseImageAudit;
import com.pathdx.model.SlideDetails;
import com.pathdx.repository.*;
import com.pathdx.service.SlideDetailService;
import com.pathdx.utils.CommonUtil;
import com.pathdx.utils.EmailUtil;
import com.pathdx.utils.GoogleCloudStorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

@Component
@Slf4j
public class SlideDetailServiceImpl implements SlideDetailService {

    @Autowired
    private SlideDetailRepository slideDetailRepository;

    @Autowired
    private SlideStainRepository slideStainRepository;

    @Autowired
    private SlideStainPanelRepository slideStainPanelRepository;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    CaseDetailsRepository caseDetailsRepository;

    @Autowired
    GoogleCloudStorageUtil googleCloudStorageUtil;

    @Autowired
    CaseImageAuditRepository caseImageAuditRepository;

    @Value("${caseImageBucket}")
    private String caseImageBucket;

    @Value("${tiled.bucket.name}")
    private String tiledBucket;

    @Autowired
    CommonUtil commonUtil;


    @Override
    public ResponseDto<List<SlideDetailsDto>> getSlideDetails(Long caseDetailId) {
        ResponseDto<List<SlideDetailsDto>> responseDto = new ResponseDto<>();
        List<SlideDetails> slideDetails = slideDetailRepository.findByCaseDetailId(caseDetailId);
        List<SlideDetailsDto> slideDetailsDto = convertListModelToDto(slideDetails);
        responseDto.setResponse(slideDetailsDto);
        return responseDto;
    }

    @Override
    public ResponseDto<SlideResDto> saveSlideDetails(SlideDto slideDto) {
        ResponseDto<SlideResDto> responseDto = new ResponseDto<>();
        SlideResDto slideResDto = new SlideResDto();
        List<SlideDetails> slideDetails = slideDto.getSlideDetails();
        List<SlideDetails> lstSlideDetails = new ArrayList<SlideDetails>();
        for(SlideDetails slideDetail : slideDetails) {
            Optional<SlideDetails> slideDetailsDB = slideDetailRepository.findById(slideDetail.getId());
            if(slideDetailsDB.isPresent()) {
                SlideDetails slideDetail1 = slideDetailsDB.get();
                slideDetail1.setRescanFlag(1);
                SlideDetails slide = slideDetailRepository.save(slideDetail1);
                lstSlideDetails.add(slide);
            }
            List<SlideDetailsDto> slideDetailsDto = convertListModelToDto(lstSlideDetails);
            slideResDto.setSlideDetailsDtos(slideDetailsDto);
            responseDto.setResponse(slideResDto);
        }
        return responseDto;
    }


    @Override
    public ResponseDto getData(String barCode, String labid, String accessionId, String caseId) {

        ResponseDto responseDto = new ResponseDto();
        SVSDataResponseDTO svsDataResponseDTO = new SVSDataResponseDTO();
        Optional<CaseImageAudit> caseImageAudit =null;
        if(labid !=null){
            caseImageAudit = caseImageAuditRepository.findCaseImageAuditByBarcodeIdAndLabId(barCode,labid);
        }else{
            caseImageAudit = caseImageAuditRepository.findCaseImageAuditByBarcodeId(barCode);
        }
        if(caseImageAudit.isPresent()){
            CaseImageAudit imageAudit = caseImageAudit.get();
            svsDataResponseDTO.setHeight(imageAudit.getHeight());
            svsDataResponseDTO.setWidth(imageAudit.getWidth());
            svsDataResponseDTO.setMpp(imageAudit.getMpp());
            svsDataResponseDTO.setBucketName(caseImageBucket);
            svsDataResponseDTO.setBarcode(imageAudit.getBarcodeId());
            if(accessionId!=null){
                svsDataResponseDTO.setAccessionId(accessionId);
            }

        }
        svsDataResponseDTO.setBucketName(tiledBucket);
        if(caseId !=null){

            CaseDetails caseDetails = caseDetailsRepository.findByCaseId(caseId);
            Map<String, Map<String,List<URL>>> responseMap = new HashMap<>();

            Map<String,List<URL>>  labelmap = new HashMap<>();

            if(caseDetails !=null && caseDetails.getSlideDetails()!=null){
                List<SlideDetails> slideDetails = caseDetails.getSlideDetails();
                for (SlideDetails details:slideDetails ) {
                    List<URL> labels = googleCloudStorageUtil.getListOfOjectInBucket(caseImageBucket,labid+"/"+details.getBarCodeid()+"/slide_image/label/");
                    labelmap.put(details.getBarCodeid(),labels);
                }
                responseMap.put("label",labelmap);
                svsDataResponseDTO.setLabelListMap(responseMap);
            }
            svsDataResponseDTO.setAccessionId(accessionId);

        }


        responseDto.setResponse(svsDataResponseDTO);
        responseDto.setStatusCode(HttpStatus.OK.value());
        responseDto.setSuccessMsg("Success");
        return responseDto;
    }


    private List<SlideDetailsDto> convertListModelToDto(List<SlideDetails> lstSlideDetails) {
        List<SlideDetailsDto> lstSlideDetailsDto = new ArrayList<SlideDetailsDto>();
        for(SlideDetails slideDetails : lstSlideDetails) {
            SlideDetailsDto slideDetailsDto = new SlideDetailsDto();
            slideDetailsDto.setId(slideDetails.getId());
            slideDetailsDto.setBarcodeId(slideDetails.getBarCodeid());
            slideDetailsDto.setBlockId(slideDetails.getBlockId());
            slideDetailsDto.setRescanFlag(slideDetails.getRescanFlag());
            slideDetailsDto.setStain(slideDetails.getStain());
            slideDetailsDto.setSpecimenId(slideDetails.getSpecimenId());
            CaseDetailsDto caseDetailsDto = new CaseDetailsDto();
            caseDetailsDto.setId(slideDetails.getCaseDetails().getId());
            caseDetailsDto.setCaseId(slideDetails.getCaseDetails().getCaseId());
            slideDetailsDto.setCaseDetail(caseDetailsDto);
            slideDetailsDto.setScannedDate(slideDetails.getScannedDate());
            slideDetailsDto.setCreatedDate(slideDetails.getCreatedDate());
            slideDetailsDto.setLastModifiedDate(slideDetails.getLastModifiedDate());
            lstSlideDetailsDto.add(slideDetailsDto);
        }
        return lstSlideDetailsDto;
    }
}
