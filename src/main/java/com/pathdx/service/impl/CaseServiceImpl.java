package com.pathdx.service.impl;


import com.pathdx.constant.CommonConstants;
import com.pathdx.dto.requestDto.CaseDetailsDto;
import com.pathdx.dto.requestDto.ThumbnailDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.exception.LabNotFoundException;
import com.pathdx.exception.OrderMessageNotFoundException;
import com.pathdx.model.*;
import com.pathdx.repository.*;
import com.pathdx.repository.reposervice.OrderMessageRepoService;
import com.pathdx.service.CaseService;
import com.pathdx.utils.CaseListingStatus;
import com.pathdx.utils.GoogleCloudStorageUtil;
import com.pathdx.utils.ValidationsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.pathdx.constant.CommonConstants.SUPER_ADMIN;
import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Component
@Profile("!demo")
@Slf4j
public class CaseServiceImpl implements CaseService {
    @Autowired
    CaseDetailsRepository caseDetailsRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PatientsRepository patientsRepository;

    @Autowired
    ObservationsRepository observationsRepository;

    @Autowired
    CaseCommentsRepository caseCommentsRepository;

    @Autowired
    PhysiciansRepository physiciansRepository;

    @Autowired
    OrderMessagesRepository orderMessagesRepository;

    @Autowired
    GoogleCloudStorageUtil googleCloudStorageUtil;

    @Autowired
    LabDetailRepository labDetailRepository;

    @Autowired
    OrderMessageRepoService orderMessageRepoService;

    @Autowired
    UserSlideRepository userSlideRepository;

    @Autowired
    SlideDetailRepository slideDetailRepository;

    @Value("${caseImageBucket}")
    private String caseImageBucket;
    @Override
    public ResponseDto<CaseResponseDto> getCaseInfo(Optional<Long> caseId, Long orderMessageId,String labId) throws Exception {
        ResponseDto<CaseResponseDto> responseDto = new ResponseDto<>();

        Optional<OrderMessages> orderMessages = orderMessagesRepository.findById(orderMessageId);

        if(!orderMessages.isPresent())
            throw new OrderMessageNotFoundException();
        String assignedUser = orderMessages.get().getUserModels()
                .stream()
                .map(UserModel::getEmail)
                .collect(Collectors.joining(","));

        Patients patients = orderMessages.get().getPatients();
        List<Observations> observationsList = observationsRepository.findByOrderMessages(orderMessages.get());
        Map<String, String> observations = observationsList.stream()
                .collect(Collectors.toMap(Observations::getIdentifier,Observations::getValue));

        Physicians physicians = orderMessages.get().getPhysicians();
        CaseResponseDto caseResponseDto = mapperCaseRespone(patients, observations, orderMessages.get(),
                physicians );
       if(StringUtils.isNotBlank(assignedUser))
        caseResponseDto.setAssignedUser(assignedUser);
        if(caseId.isPresent()) {
            Optional<CaseDetails> caseDetails = caseDetailsRepository.findById(caseId.get());

            if (caseDetails.isPresent()) {
               if(StringUtils.isNotBlank(caseDetails.get().getReportGeneratedBy()))
                caseResponseDto.setReportGeneratedFlag(true);
               else
                caseResponseDto.setReportGeneratedFlag(false);
                List<CaseComments> caseCommentsList = caseCommentsRepository.findByCaseDetails(caseDetails.get());
                CaseCommentsDto caseCommentsDto = new CaseCommentsDto();
                Optional.ofNullable(caseCommentsList)
                        .ifPresent(caseComments -> mapCaseComments(caseCommentsList, caseCommentsDto));
                caseResponseDto.setCaseComments(caseCommentsDto);
            }
        }
        try {
            String accessionId = orderMessages.get().getAccessionId();
            String blobName = labId + "/" + accessionId + "/pdf_requisition/" + accessionId + ".pdf";
            boolean flag = googleCloudStorageUtil.isFileExists(blobName,caseImageBucket);
            if(flag) {
                URL reqPdfSignedURL = googleCloudStorageUtil.generateSignedUrl(caseImageBucket, blobName);
                caseResponseDto.setReqPdfSingedUrl(reqPdfSignedURL);
            }
        }catch (Exception e){
            log.info("pdf not found");
            log.error(e.getMessage());
        }
        responseDto.setResponse(caseResponseDto);
        return responseDto;
    }



    @Override
    public ResponseDto getImagesForCase(String labId , String accessionId, String caseId, String email) {
        ResponseDto responseDto = new ResponseDto();

        CaseDetails caseDetails = caseDetailsRepository.findByCaseId(caseId);

        List<URL> clinical_image;
        List<URL> grossing_image;
        List<URL> miscellaneous_image;

        Map<String,Map<String,List<URL>>> responseMap = new HashMap<>();

        Map<String,List<URL>>  imageMap = new HashMap<>();
        Map<String,List<URL>>  thumbnailsmap = new HashMap<>();

        clinical_image = googleCloudStorageUtil.getListOfOjectInBucket(caseImageBucket,labId+"/"+accessionId+"/clinical_image/");
        imageMap.put("clinicalImage",clinical_image);

        grossing_image = googleCloudStorageUtil.getListOfOjectInBucket(caseImageBucket,labId+"/"+accessionId+"/grossing_image/");
        imageMap.put("grossingImage",grossing_image);
        miscellaneous_image =googleCloudStorageUtil.getListOfOjectInBucket(caseImageBucket,labId+"/"+accessionId+"/miscellaneous_image/annotated_image/"+caseId+"/");
        imageMap.put("miscellaneousImage",miscellaneous_image);
        responseMap.put("mise",imageMap);
       Long userId =  usersRepository.findUserModelByEmail(email).get().getId();
        if(caseDetails !=null && caseDetails.getSlideDetails()!=null){
            List<SlideDetails> slideDetails = caseDetails.getSlideDetails();
            for (SlideDetails details:slideDetails ) {

                List<URL> barcodes = googleCloudStorageUtil.getListOfOjectInBucket(caseImageBucket,labId+"/"+details.getBarCodeid()+"/slide_image/thumbnail_image/");
                Long count = userSlideRepository.findByBarcodeIdAndUserId(details.getBarCodeid(), userId);
                if(count == 0)
                    thumbnailsmap.put(details.getBarCodeid()+"_00",barcodes);
                else{
                    thumbnailsmap.put(details.getBarCodeid()+"_11",barcodes);
                }
               // thumbnailsmap.put(details.getBarCodeid(),barcodes);

            }
            responseMap.put("thumbnails",thumbnailsmap);
        }

        responseDto.setResponse(responseMap);
        responseDto.setStatusCode(HttpStatus.OK.value());

        return responseDto;
    }
    @Override
    public ResponseDto<CaseListingDto> getCaseListing(String userMail, String labId, CaseListingStatus status, Optional<String> accessionId, int pageNo, int pageSize) throws LabNotFoundException, Exception {

        ResponseDto<Map<String, Set<CaseDetailsDto>>> responseDto = new ResponseDto<>();
        try{
                if(accessionId.isPresent()){
                   return getAccessionId(labId, accessionId.get(), status, pageNo, pageSize);
                }
        List<String> role = getRole(userMail);
                if(role.contains(CommonConstants.LAB_ADMIN) || role.contains(SUPER_ADMIN)){
                    return  getCaseListingForAdmin(userMail, labId, status, pageNo, pageSize);
                }else{
                    return   getCaseListingForReviewer(userMail, labId, status, pageNo, pageSize);
                }} catch (Exception le){
                    throw le;
                }


    }

    private ResponseDto<CaseListingDto>  getCaseListingForReviewer(String userMail, String labId,
                                                                                     CaseListingStatus status, int pageNo,
                                                                                     int pageSize)  throws Exception{
        ResponseDto<CaseListingDto> responseDto = new ResponseDto<>();
        CaseListingDto caseListingDto = new CaseListingDto();
        try {
            Pageable paging = PageRequest.of(pageNo, pageSize,  Sort.by(Sort.Direction.ASC,"createdDate"));
            Page<OrderMessages> orderMessagesPage = null;
            List<String> accessionIds = new ArrayList<>();


            UserModel userModel = usersRepository.findUserModelByEmail(userMail).get();
            LabDetail labDetail = labDetailRepository.findByLabid(labId).get();
            ValidationsUtils.labValidation(labId, userModel);
            Set<UserModel> userModels = new HashSet<>();
            userModels.add(userModel);
           if(status.equals(CaseListingStatus.CLOSEDCASES))
            orderMessagesPage = orderMessagesRepository.findByUserModels(userModels, "closed", "F",labDetail, paging);
           else if (status.equals(CaseListingStatus.ALLCASES)) {
               orderMessagesPage = orderMessagesRepository.findByUserModelsAndLabDetail(userModels, "closed", "F",labDetail, paging);

           }

            if (!orderMessagesPage.hasContent()) {
                log.info("no data");
            }
            Map<String, Set<CaseDetailsDto>> caseDetailsMap = new HashMap<>();
            if(!orderMessagesPage.hasContent())return null;
            for( OrderMessages orderMessages : orderMessagesPage.get().collect(Collectors.toSet())){
                Long id = orderMessages.getId();
                String accessionId = orderMessages.getAccessionId();
                Set<CaseDetails> caseDetailsList = caseDetailsRepository.findByOrderMessages(orderMessages);
                Set<String> caseIds = new HashSet<>();
                List<CaseDetails> caseDetails1 = caseDetailsList.stream()
                        .filter(e -> caseIds.add(e.getCaseId())).toList();
                Set<CaseDetailsDto> caseDetailsDtos = caseDetails1.stream()
                        .map((CaseDetails caseDetails) -> caseDetailsToDTO(caseDetails, id))
                        .collect(Collectors.toSet());
                if(caseDetailsDtos.size() == 0){
                    CaseDetailsDto caseDetailsDto = new CaseDetailsDto();
                    caseDetailsDto.setOrderMessageId(orderMessages.getId());
                    caseDetailsDtos.add(caseDetailsDto);
                }
                caseDetailsMap.put(accessionId, caseDetailsDtos);
                //counter++;
            }
            caseListingDto.setAccessionList(caseDetailsMap);
            caseListingDto.setCount(orderMessagesPage.getTotalElements());
            responseDto.setResponse(caseListingDto);
        }
        catch (Exception e){
            throw new Exception("exception occured", e);
        }
        return responseDto;
    }


    private ResponseDto<CaseListingDto> getCaseListingForAdmin(String userMail, String labId, CaseListingStatus status, int pageNo, int pageSize) throws Exception {
        ResponseDto<CaseListingDto> responseDto = new ResponseDto<>();
        CaseListingDto caseListingDto = new CaseListingDto();
        try {
            Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC,"createdDate"));
            Page<OrderMessages> orderMessagesPage = null;
            List<String> accessionIds = new ArrayList<>();

            UserModel userModel = usersRepository.findUserModelByEmail(userMail).get();

            LabDetail labDetail = labDetailRepository.findByLabid(labId).get();

           // ValidationsUtils.labValidation(labId, userModel);
            if(status.equals(CaseListingStatus.CLOSEDCASES))
            orderMessagesPage = orderMessagesRepository.findByLabDetailAndCaseStatus(labDetail, "closed", "F", paging);
            else if (status.equals(CaseListingStatus.ALLCASES)) {
                orderMessagesPage = orderMessagesRepository.findByCaseStatusAndLabDetail("closed", "F", labDetail, paging);
            }
            else if (status.equals(CaseListingStatus.NEWCASES)) {
                //orderMessagesPage = orderMessagesRepository.findNewCasesAndLabDetail("closed", "F", labId, paging);
                orderMessagesPage = orderMessageRepoService.findNewCasesAndLabDetail(labId, paging);
            }

            if (!orderMessagesPage.hasContent()) {
                log.info("no data");
            }
            if(!orderMessagesPage.hasContent())return new ResponseDto(responseDto, "", SUCCESS_MESSAGE, HttpStatus.NO_CONTENT.value());;
            Map<String, Set<CaseDetailsDto>> caseDetailsMap = new HashMap<>();
            for( OrderMessages orderMessages : orderMessagesPage.get().collect(Collectors.toSet())){
                Set<CaseDetails> caseDetailsList = caseDetailsRepository.findByOrderMessages(orderMessages);

                Long id = orderMessages.getId();
                String accessionId = orderMessages.getAccessionId();
                log.info("casedetails from db are:{}, order message id is {}", caseDetailsList,id);

                Set<String> caseIds = new HashSet<>();
                List<CaseDetails> caseDetails1 = caseDetailsList.stream()
                        .filter(e -> caseIds.add(e.getCaseId())).toList();
                Set<CaseDetailsDto> caseDetailsDtos = caseDetails1.stream()
                        .map( caseDetails -> caseDetailsToDTO(caseDetails, id))
                        .collect(Collectors.toSet());
                log.info("dto object is:  {}", caseDetailsDtos);
                if(caseDetailsDtos.size() == 0){
                    CaseDetailsDto caseDetailsDto = new CaseDetailsDto();
                    caseDetailsDto.setOrderMessageId(orderMessages.getId());
                    caseDetailsDtos.add(caseDetailsDto);
                }
                caseDetailsMap.put(accessionId, caseDetailsDtos);
                //counter++;
            }
            caseListingDto.setAccessionList(caseDetailsMap);
            caseListingDto.setCount(orderMessagesPage.getTotalElements());
            responseDto.setResponse(caseListingDto);
        }
        catch (Exception e){
            throw new Exception("exception occured", e);
        }
        return responseDto;
    }

    public ResponseDto<CaseListingDto> getAccessionId(String labId, String accessionId, CaseListingStatus status, int pageNo, int pageSize) {
        ResponseDto<CaseListingDto> responseDto = new ResponseDto<>();
        CaseListingDto caseListingDto = new CaseListingDto();

        Page<OrderMessages> orderMessagesPage = orderMessageRepoService.findByAccessionId(accessionId, labId, status,pageNo, pageSize);

        if (!orderMessagesPage.hasContent()) {
            log.info("no data");
        }
        Map<String, Set<CaseDetailsDto>> caseDetailsMap = new HashMap<>();
        if(!orderMessagesPage.hasContent())return null;
        for( OrderMessages orderMessages : orderMessagesPage.get().collect(Collectors.toSet())){
            Long id = orderMessages.getId();
            String accessId = orderMessages.getAccessionId();
            Set<CaseDetails> caseDetailsList = caseDetailsRepository.findByOrderMessages(orderMessages);
            Set<String> caseIds = new HashSet<>();
            List<CaseDetails> caseDetails1 = caseDetailsList.stream()
                    .filter(e -> caseIds.add(e.getCaseId())).toList();
            Set<CaseDetailsDto> caseDetailsDtos = caseDetails1.stream()
                    .map((CaseDetails caseDetails) -> caseDetailsToDTO(caseDetails, id))
                    .collect(Collectors.toSet());
            if(caseDetailsDtos.size() == 0){
                CaseDetailsDto caseDetailsDto = new CaseDetailsDto();
                caseDetailsDto.setOrderMessageId(orderMessages.getId());
                caseDetailsDtos.add(caseDetailsDto);
            }
            caseDetailsMap.put(accessId, caseDetailsDtos);
        }
        caseListingDto.setAccessionList(caseDetailsMap);
        caseListingDto.setCount(orderMessagesPage.getTotalElements());
        responseDto.setResponse(caseListingDto);
        return responseDto;

    }

    @Override
    public Map<String, Long > getCaseCount(String userMail, String labId) {

        Map<String, Long> caseCount = new HashMap<>();
        String id = null;
        List<String> roles = getRole(userMail);
        if(roles.contains(SUPER_ADMIN) || roles.contains(CommonConstants.LAB_ADMIN) ) {
            log.info("in service call for super admin");
            Long  count = orderMessageRepoService.findCaseCount(labId, Optional.ofNullable(id), "allcases");
            caseCount.put("allcases", count);
             count = orderMessageRepoService.findCaseCount(labId, Optional.ofNullable(id), "closed");
            caseCount.put("closedcases", count);
            log.info("done with count retrieval");
        }else{
            log.info("in service call for reviewer");
            Long count = orderMessageRepoService.findCaseCount(labId, Optional.ofNullable(userMail), "allcases");
            caseCount.put("allcases", count);
            count = orderMessageRepoService.findCaseCount(labId, Optional.ofNullable(userMail), "closed");
            caseCount.put("closedcases", count);
            log.info("done with count retrieval");
        }
return caseCount;
    }



    @Override
    public String saveUserSlide(ThumbnailDto thumbnailDto, String email) throws Exception {
        UserSlideModel userSlideModel = new UserSlideModel();
        try{
        UserModel userModel = usersRepository.findUserModelByEmail(email).get();
        userSlideModel.setBarcodeId(thumbnailDto.getBarcodeId());
        userSlideModel.setUserModel(userModel);
        userSlideModel.setLabId(thumbnailDto.getLabId());
        userSlideRepository.save(userSlideModel);
            return "updated thumbnail";}
        catch (Exception e){
            throw e;
        }

    }

    private CaseDetailsDto caseDetailsToDTO(CaseDetails caseDetails, Long id){
        log.info("case details inputs are ::casedetails:{}, order message id is:{}", caseDetails.getId(), id);
        CaseDetailsDto caseDetailsDto = new CaseDetailsDto();
        caseDetailsDto.setId(caseDetails.getId());
        caseDetailsDto.setOrderMessageId(id);
        caseDetailsDto.setCaseId(caseDetails.getCaseId());
        caseDetailsDto.setReportGenerated(StringUtils.isNotBlank(caseDetails.getReportGeneratedBy()));
        return caseDetailsDto;
    }
    private CaseResponseDto mapperCaseRespone(Patients patients, Map<String, String> observations,
                                              OrderMessages orderMessages, Physicians physicians) {
        CaseResponseDto caseResponseDto = new CaseResponseDto();
        PatientsDto patientsDto = new PatientsDto();
        OrderMessageDto orderMessageDto = new OrderMessageDto();

        List<String> patientNameList = Arrays.asList(patients.getFirstName(), patients.getMiddleName(), patients.getLastName());
        Optional.of(patientNameList.stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(StringUtils.SPACE)))
                .ifPresent(patientsDto::setPatientName);
        Optional.ofNullable(patients.getDob())
                .ifPresent(patientsDto::setDob);
        Optional.ofNullable(patients.getGender())
                .ifPresent(patientsDto::setGender);
        Optional.ofNullable(patients.getEthnicity())
                .ifPresent(patientsDto::setEthnicity);
        Optional.ofNullable(patients.getMrn())
                .ifPresent(patientsDto::setMRN);
        caseResponseDto.setPatient(patientsDto);
        Optional.ofNullable(orderMessages.getCaseAcct())
                .ifPresent(orderMessageDto::setCaseAccnt);
        caseResponseDto.setObservations(observations);

        Optional.ofNullable(orderMessages.getHospital())
                .ifPresent(orderMessageDto::setClientName);
        /*Optional.ofNullable(orderMessages.getCaseStatus())
                .ifPresent(orderMessageDto::setCaseStatus);*/
        if(StringUtils.isNotBlank(orderMessages.getCaseStatus())){
            if(StringUtils.equalsIgnoreCase(orderMessages.getCaseStatus(), "F"))
                orderMessageDto.setCaseStatus("closed");
            else
                orderMessageDto.setCaseStatus(orderMessages.getCaseStatus());
        }
        caseResponseDto.setOrderMessage(orderMessageDto);
        Optional.ofNullable(physicians.getPhone())
                .ifPresent(caseResponseDto::setPhysicianPhone);
        List<String> physicianNameList = Arrays.asList(physicians.getFirstName(), physicians.getMiddleName(), physicians.getLastName());
        Optional.of(physicianNameList.stream()
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.joining(StringUtils.SPACE)))
                .ifPresent(caseResponseDto::setPhysicianName);

return caseResponseDto;

    }

    private static void mapCaseComments(List<CaseComments> caseCommentsList, CaseCommentsDto caseCommentsDto) {
        Optional.of(caseCommentsList.stream()
                .map(CaseComments::getFirstAdditionalDiagnosis)
                        .collect(Collectors.toList()))
                .ifPresent(caseCommentsDto::setFirstDiagnosis);
        Optional.of(caseCommentsList.stream()
                        .map(CaseComments::getSecondAdditionalDiagnosis)
                        .collect(Collectors.toList()))
                .ifPresent(caseCommentsDto::setCaseSummary);
        Optional.of(caseCommentsList.stream()
                        .map(CaseComments::getThirdAdditionalDiagnosis)
                        .collect(Collectors.toList()))
                .ifPresent(caseCommentsDto::setClinicalHistory);
        Optional.of(caseCommentsList.stream()
                        .map(CaseComments::getFinalDiagnosis)
                        .collect(Collectors.toList()))
                .ifPresent(caseCommentsDto::setFinalDiagnosis);
    }

    private List<String> getRole(String email) {
        return usersRepository.findUserModelByEmail(email)
                .get()
                .getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();

    }

}
