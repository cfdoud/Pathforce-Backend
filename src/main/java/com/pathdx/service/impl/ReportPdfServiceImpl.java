package com.pathdx.service.impl;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.pathdx.constant.AuditAction;
import com.pathdx.dto.requestDto.AnnotatedImageReqDto;
import com.pathdx.dto.requestDto.ReportsReqDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.*;
import com.pathdx.pdfGenerate.ReportDetails;
import com.pathdx.repository.*;
import com.pathdx.service.*;
import com.pathdx.utils.AuditLogUtil;
import com.pathdx.utils.GoogleCloudStorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;

@Component
@Slf4j
public class ReportPdfServiceImpl implements ReportPdfService {

    @Value("${caseImageBucket}")
    private String caseImageBucket;

    @Value("${bucketName}")
    private String bucketName;

    @Autowired
    GoogleCloudStorageUtil googleCloudStorageUtil;

    @Autowired
    OrderMessagesRepository orderMessagesRepository;

    @Autowired
    LabHeadingsRepository labHeadingsRepository;

    @Autowired
    LabDetailRepository labDetailRepository;

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
    CaseDetailsRepository caseDetailsRepository;

    @Autowired
    AuditLogUtil auditLogUtil;


    public List<Object> getLabHeadings(String labId) throws Exception {
        List<Object> list = new ArrayList<>();
        Optional<LabDetail> labDetail = labDetailRepository.findByLabid(labId);
        LabHeadingsResponseDto labHeadingsResponseDto = new LabHeadingsResponseDto();
        if(labDetail.isPresent()) {
            Optional<LabHeadings> labHeadings = labHeadingsRepository.findByLabDetail(labDetail.get());
            if(labHeadings.isPresent()) {
                //labHeadingsResponseDto = convertToLabHeadResponseDto(labHeadings.get());
                //LabResponseDto labDetailsRequestDto = convertToLabResponseDto(labDetail.get());
                list.add(labDetail.get());
                list.add(labHeadings.get());
                log.info("labHeadings..{}", labHeadings);
            }
        }
        return list;
    }

    public UserModelResponseDto getUserDetails(String email){
        UserModelResponseDto userResDto = new UserModelResponseDto();
        Optional<UserModel> user = usersRepository.findUserModelByEmail(email);
        if(user.isPresent()) {
            userResDto = convertUserResDto(user.get());
        }
        return userResDto;
    }

    public OrderMessageDto getOrderMessage(Long id) {
        Optional<OrderMessages> orderMessages = orderMessagesRepository.findById(id);
        OrderMessageDto orderMessageDto = new OrderMessageDto();
        if(orderMessages.isPresent()){
            orderMessageDto.setDateReported(String.valueOf(orderMessages.get().getDateReported()));
        }
        return orderMessageDto;
    }

    public ResponseDto<CaseResponseDto> getCaseInfoForReport(Optional<String> caseId, Long orderMessageId) {
        ResponseDto<CaseResponseDto> responseDto = new ResponseDto<>();

        OrderMessages orderMessages = orderMessagesRepository.getReferenceById(orderMessageId);
        Patients patients = patientsRepository.findByOrderMessages(orderMessages);
        List<Observations> observationsList = observationsRepository.findByOrderMessages(orderMessages);
        Map<String, String> observations = observationsList.stream()
                .collect(Collectors.toMap(Observations::getIdentifier,Observations::getValue));
        Physicians physicians = physiciansRepository.findByOrderMessages(orderMessages);
        CaseResponseDto caseResponseDto = mapperCaseRespone(patients, observations, orderMessages,
                physicians );
        if(caseId.isPresent()) {
            CaseDetails caseDetails = caseDetailsRepository.findByCaseId(caseId.get());

            List<CaseComments> caseCommentsList = caseCommentsRepository.findByCaseDetails(caseDetails);
            CaseCommentsDto caseCommentsDto = new CaseCommentsDto();
            Optional.ofNullable(caseCommentsList)
                    .ifPresent(caseComments -> mapCaseComments(caseCommentsList, caseCommentsDto));
            caseResponseDto.setCaseComments(caseCommentsDto);

        }
        responseDto.setResponse(caseResponseDto);
        return responseDto;
    }

    @Override
    public ResponseDto<ReportResponseDto> createReport(ReportsReqDto reportModelDto) throws Exception {
        ResponseDto<ReportResponseDto> responseDto = new ResponseDto<>();
        ReportResponseDto reportResDto = new ReportResponseDto();
        List<Object> list = getLabHeadings(reportModelDto.getLabId());
        LabDetail labDetailModal = (LabDetail) list.get(0);
        LabHeadings labHeadingsModal = (LabHeadings) list.get(1);
        log.info("labResponseDto is {} ", labDetailModal);
        log.info("labHeadingsResponseDto is {} ", labHeadingsModal);

        UserModelResponseDto userResponseDto = getUserDetails(reportModelDto.getEmail());
        log.info("userResponseDto is {} ", userResponseDto);

        ResponseDto<CaseResponseDto> caseResponseDto = getCaseInfoForReport(Optional.of(reportModelDto.getCaseId()), reportModelDto.getOrderMessageId());
        log.info("caseResponseDto is {} ", caseResponseDto);

        OrderMessageDto orderMessageDtoResponseDto = getOrderMessage(reportModelDto.getOrderMessageId());
        log.info("orderMessageDtoResponseDto is {} ", orderMessageDtoResponseDto);

        try {
            //Create Object Of Report Heading Page
            String labName = labDetailModal.getLabName();
            String labWebsite = labDetailModal.getLabWebsite();
            if(labWebsite==null){
                labWebsite="";
            }
            String labContactNo = labDetailModal.getLabContactNo();
            String resultID = reportModelDto.getCaseId();

            //Lab Address
            String street = labDetailModal.getStreet();
            if(street==null){
                street = "";
            }
            String city = labDetailModal.getCity();
            if(city==null){
                city = "";
            }
            String state = labDetailModal.getState();
            if(state==null){
                state = "";
            }
            String zip = ""+labDetailModal.getZip();
            if(zip==null){
                zip = "";
            }
            String labFullAddress = street+" "+city+" "+state+" "+zip;
            String accessionId = caseResponseDto.getResponse().getOrderMessage().getAccessionId();
            String patientName = caseResponseDto.getResponse().getPatient().getPatientName();
            String physicianName = caseResponseDto.getResponse().getPhysicianName();
            String mrn = caseResponseDto.getResponse().getPatient().getMRN();

            String clinicalHistory = "";
            if(caseResponseDto.getResponse().getCaseComments().getClinicalHistory().size()>0){
                clinicalHistory = caseResponseDto.getResponse().getCaseComments().getClinicalHistory().get(0);
            }

            if(clinicalHistory==null){
                clinicalHistory="";
            }

            String finalDiagnosis = "";
            if(caseResponseDto.getResponse().getCaseComments().getFinalDiagnosis().size()>0){
                finalDiagnosis = caseResponseDto.getResponse().getCaseComments().getFinalDiagnosis().get(0);
            }
            //clinicalHistory = "Biopsies are most often done to either conrm or rule out a suspicion of cancer. However, biopsies are also performed to diagnosis other causes of your symptoms including: Inammatory disorders, such as in the kidney (nephritis) or the liver hepatitis). Infections, such as tuberculosis (hepatitis). Infections, such as tuberculosis";
            if(finalDiagnosis==null){
                finalDiagnosis = "";
            }
            //finalDiagnosis = "Biopsies are most often done to either conrm or rule out a suspicion of cancer. However, biopsies are also performed to diagnosis other causes of your symptoms including: Inammatory disorders, such as in the kidney (nephritis) or the liver  (hepatitis). Infections, such as tuberculosis (hepatitis). Infections, such as tuberculodf";
            String pathologistName = userResponseDto.getFirstName()+""+userResponseDto.getMiddleName()+" "+userResponseDto.getLastName();
            String pathologistRole ="Lab Reviewer";
            String pathologistDegree = userResponseDto.getDegree();
            String mobileNumber = userResponseDto.getMobilePh();
            String comments = reportModelDto.getComments();
            ReportDetails reportDetails = new ReportDetails();

            String firstHeading = labHeadingsModal.getFirstHeading();
            String secondHeading = labHeadingsModal.getSecondHeading();
            String thirdHeading = labHeadingsModal.getThirdHeading();
            String fourthHeading = labHeadingsModal.getFourthHeading();
            String fifthHeading = labHeadingsModal.getFifthHeading();
            String sixthHeading = labHeadingsModal.getSixthHeading();
            String seventhHeading = labHeadingsModal.getSeventhHeading();

            String reportedDate = orderMessageDtoResponseDto.getDateReported();
            //Image ArrayList

            String labImageBlobName = reportModelDto.getLabId()+"/lab_docs/logo/logo.png";

            ArrayList<String> images = new ArrayList<String>();
            URL labImageUrl = googleCloudStorageUtil.generateSignedUrl(bucketName,labImageBlobName);
            String labImage = labImageUrl.toString();
            images.add(labImage);

            //Use Methods to set values
            //Report Headings
            reportDetails.ReportHeadings(firstHeading, secondHeading, thirdHeading, fourthHeading, fifthHeading, sixthHeading, seventhHeading);
            //Lab Details
            reportDetails.LabDetails(labName, labContactNo, labWebsite, labImage, labFullAddress);
            //PathologistDetails
            reportDetails.PathologistDetails(pathologistName, pathologistDegree, pathologistRole);
            //Report Dates
            reportDetails.ReportDate(String.valueOf(reportedDate));
            //Case Details
            reportDetails.CaseDetails(patientName, physicianName, accessionId, resultID, mrn, mobileNumber, comments, clinicalHistory, finalDiagnosis, images);

            //HTML to convert to PDF
            //Document

            String totalHTMLBlobName = reportModelDto.getLabId()+"/report_templates/totalPages.html";
            byte[] byteArr = googleCloudStorageUtil.readFile(totalHTMLBlobName,bucketName);
            String strHtml = new String(byteArr);
            Document doc = Jsoup.parse(strHtml,"utf-8");

            updateDocument(doc,reportDetails,labImage);
            // Add Images
            Map<String,String> map = renderAttachedImages(reportModelDto.getAttachedImages());
            if(map.containsKey("firstRow")) {
                Elements eImageTDs1 = doc.getElementsContainingOwnText("{{imageTDs1}}").html(map.get("firstRow"));
            }else{
                Elements eImageTDs1 = doc.getElementsContainingOwnText("{{imageTDs1}}").html("");
            }
            if(map.containsKey("secondRow")) {
                Elements eImageTDs2 = doc.getElementsContainingOwnText("{{imageTDs2}}").html(map.get("secondRow"));
            }else{
                Elements eImageTDs2 = doc.getElementsContainingOwnText("{{imageTDs2}}").html("");
            }

            //Pathforce Logo
            String PathForceTechBlobName = reportModelDto.getLabId()+"/report_templates/PathForceTech.png";
            URL PathForceTechURL = googleCloudStorageUtil.generateSignedUrl(bucketName,PathForceTechBlobName);

            String pathForceLogo = PathForceTechURL.toString();
            Elements epathforceLogo = doc.getElementsByAttributeValueContaining("src", "{{PathforceLogo}}").attr("src", pathForceLogo);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(doc.outerHtml(), baos);


            String folderName = reportModelDto.getLabId()+"/"+accessionId+"/reports_pdf/"+resultID+"/Pathforce_"+resultID+".pdf";
            log.info("Start deleting existing generated pdf");
            boolean flag = googleCloudStorageUtil.deleteFile(folderName,caseImageBucket);
            log.info("End deleting existing generated pdf {}",flag);

            log.info("Upload started");
            String uploadImageLink = googleCloudStorageUtil.uploadPdfFile(baos,caseImageBucket,folderName);
            URL diagnosisPDFURL = googleCloudStorageUtil.generateSignedUrl(caseImageBucket,folderName);
            log.info("Upload ended");
            //for Audit log
            try {
                CaseDetails caseDetails = caseDetailsRepository.findByCaseId(resultID);
                ActionModel actionModel = auditLogUtil.getActions(AuditAction.REPORT_GENERATED);
                Object[] args = {accessionId, userResponseDto.getEmail()};
                String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(), args);
                auditLogUtil.saveAuditLogs(AuditAction.REPORT_GENERATED, userResponseDto.getId(),
                        caseDetails.getId(),reportModelDto.getLabId(),
                        reportModelDto.getOrderMessageId(),userResponseDto.getEmail(), msg);
            }catch (Exception e){
                log.info("Exception found while inserting for audit,{}",e.getMessage());
            }
            // for reportGenerated entry in case_details table
            try{
                CaseDetails caseDetails = caseDetailsRepository.findByCaseId(resultID);
                caseDetails.setReportGeneratedBy(userResponseDto.getEmail());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date dateObj = null;
                try {
                    dateObj = sdf.parse(sdf.format(new Date()));
                } catch (java.text.ParseException e) {
                    log.info(e.getMessage());
                }
                caseDetails.setReportGeneratedDate(dateObj);
                caseDetailsRepository.save(caseDetails);
            }catch (Exception e){
                log.info("Exception found while inserting in case_details table,{}",e.getMessage());
            }
            reportResDto.setSingedUrl(diagnosisPDFURL);
            responseDto.setResponse(reportResDto);
            responseDto.setSuccessMsg(SUCCESS_MESSAGE);
            responseDto.setStatusCode(HttpStatus.OK.value());
        } catch (Exception io) {
            log.error("error",io);
            responseDto.setSuccessMsg(SUCCESS_MESSAGE);
            responseDto.setStatusCode(HttpStatus.NO_CONTENT.value());
        }
        return responseDto;
    }

    private void updateDocument(Document doc, ReportDetails reportDetails,String labImage) {
        //Update headings in HTML
        Elements eFirstHeading = doc.getElementsContainingOwnText("{{FirstReportHeading}}").html(reportDetails.firstHeading);
        Elements eSecondHeading = doc.getElementsContainingOwnText("{{SecondReportHeading}}").html(reportDetails.secondHeading);
        Elements eThirdHeading = doc.getElementsContainingOwnText("{{ThirdReportHeading}}").html(reportDetails.thirdHeading);
        Elements eFourthHeading = doc.getElementsContainingOwnText("{{FourthReportHeading}}").html(reportDetails.fourthHeading);
        Elements eFifthHeading = doc.getElementsContainingOwnText("{{FifthReportHeading}}").html(reportDetails.fifthHeading);
        Elements eSixthHeading = doc.getElementsContainingOwnText("{{SixthReportHeading}}").html(reportDetails.sixthHeading);
        Elements eSeventhHeading = doc.getElementsContainingOwnText("{{SeventhReportHeading}}").html(reportDetails.seventhHeading);

        //Update Lab Details
        String labDetails = reportDetails.labName + "</br>" + reportDetails.labWebsite + "</br";
        Elements elabName = doc.getElementsContainingOwnText("{{LabName}}").html(labDetails);
        if(reportDetails.labPhoneNumber!=null) {
            Elements elabPhoneNumber = doc.getElementsContainingOwnText("{{LabPhoneNumber}}").html(reportDetails.labPhoneNumber);
        }
        else {
            Elements elabPhoneNumber = doc.getElementsContainingOwnText("{{LabPhoneNumber}}").html("");
        }
        if(reportDetails.labAddress!=null) {
            Elements elabAddress = doc.getElementsContainingOwnText("{{LabAddress}}").html(reportDetails.labAddress);
        }else{
            Elements elabAddress = doc.getElementsContainingOwnText("{{LabAddress}}").html("");
        }
        Elements elabLogo = doc.getElementsByAttributeValueContaining("src", "{{LabLogo}}").attr("src", labImage);

        //Update PathologistDetails
        if(reportDetails.pathologistName!=null) {
            Elements ePathologistName = doc.getElementsContainingOwnText("{{PathologistName}}").html(reportDetails.pathologistName);
        }else{
            Elements ePathologistName = doc.getElementsContainingOwnText("{{PathologistName}}").html("");
        }
        if(reportDetails.pathologistDegree!=null){
            Elements ePathologistDegree = doc.getElementsContainingOwnText("{{PathologistDegree}}").html(reportDetails.pathologistDegree);
        }else {
            Elements ePathologistDegree = doc.getElementsContainingOwnText("{{PathologistDegree}}").html("");
        }
        if(reportDetails.pathologistRole!=null) {
            Elements ePathologistRole = doc.getElementsContainingOwnText("{{PathologistRole}}").html(reportDetails.pathologistRole);
        }else {
            Elements ePathologistRole = doc.getElementsContainingOwnText("{{PathologistRole}}").html("");
        }

        //Update Report Dates
        if(reportDetails.reportedDate!=null){
            Elements eReportedDate = doc.getElementsContainingOwnText("{{ReportedDate}}").html(reportDetails.reportedDate);
        }else{
            Elements eReportedDate = doc.getElementsContainingOwnText("{{ReportedDate}}").html("");
        }

        //Elements eReceivedDate = doc.getElementsContainingOwnText("{{ReceivedDate}}").html(reportDetails.receivedDate);
        //Elements reviewedDate = doc.getElementsContainingOwnText("{{ReviewedDate}}").html(reportDetails.reviewedDate);

        //Update CaseDetails
        Elements ePatientName = doc.getElementsContainingOwnText("{{PatientName}}").html(reportDetails.patientName);
        if(reportDetails.physicianName!=null) {
            Elements ePhysicianName = doc.getElementsContainingOwnText("{{PhysicianName}}").html(reportDetails.physicianName);
        }else{
            Elements ePhysicianName = doc.getElementsContainingOwnText("{{PhysicianName}}").html("");
        }
        if(reportDetails.mrn!=null) {
            Elements eMrn = doc.getElementsContainingOwnText("{{MRN}}").html(reportDetails.mrn);
        }else{
            Elements eMrn = doc.getElementsContainingOwnText("{{MRN}}").html("");
        }
        if(reportDetails.phoneNumber!=null) {
            Elements ePhoneNumber = doc.getElementsContainingOwnText("{{PhoneNumber}}").html(reportDetails.phoneNumber);
        }else{
            Elements ePhoneNumber = doc.getElementsContainingOwnText("{{PhoneNumber}}").html("");
        }
        Elements eAccessionID = doc.getElementsContainingOwnText("{{AccessionId}}").html(reportDetails.accessionId);
        Elements eResultId = doc.getElementsContainingOwnText("{{ResultId}}").html(reportDetails.resultId);
        Elements eComment = doc.getElementsContainingOwnText("{{Comment}}").html(reportDetails.comment);
        if(reportDetails.clinicalHistory!=null) {
            Elements eClinicalHistory = doc.getElementsContainingOwnText("{{ClinicalHistory}}").html(reportDetails.clinicalHistory);
        }else{
            Elements eClinicalHistory = doc.getElementsContainingOwnText("{{ClinicalHistory}}").html("");
        }
        if(reportDetails.finalDiagnosis!=null){
            Elements eFinalDiagnosis = doc.getElementsContainingOwnText("{{FinalDiagnosis}}").html(reportDetails.finalDiagnosis);
        }else{
            Elements eFinalDiagnosis = doc.getElementsContainingOwnText("{{FinalDiagnosis}}").html("");
        }

    }

    private Map<String, String> renderAttachedImages(List<String> attachedImages) {
        String imageTDs1 = "";
        String imageTDs2 = "";
        Map<String,String> map = new HashMap<>();
        int count = 0;
        for(String imageSource: attachedImages){
            count = count+1;
            String strSignedUrl = imageSource.replace("https","http");
            try {
                String result = convertSingedURLToBase64(strSignedUrl);
                if (count < 4) {
                    imageTDs1 = imageTDs1 + "<td align='center' valign='middle'><img src='data:image/png;base64," + result + "' width=\"250\" height=\"100\" alt=\"\" /></td>";
                    map.put("firstRow",imageTDs1);
                } else {
                    imageTDs2 = imageTDs2 + "<td align='center' valign='middle'><img src='data:image/png;base64," + result + "' width=\"250\" height=\"100\" alt=\"\" /></td>";
                    map.put("secondRow",imageTDs2);
                }
            }catch (Exception e){
                log.error("Error occurred while converting signedLink to base64 url {}",e);
                continue;
            }
            log.info("count::"+count);
        }


        return map;
    }

    @Override
    public ResponseDto<byte[]> viewRequisitionPdf(ReportsReqDto reportModelDto) {
        ResponseDto<byte[]> responseDto = new ResponseDto<>();
        Optional<OrderMessages>  orderMessages = orderMessagesRepository.findById(reportModelDto.getOrderMessageId());
        if(orderMessages.isPresent()){
            String accessionId = orderMessages.get().getAccessionId();
            String blobName = reportModelDto.getLabId()+"/"+accessionId+"/pdf_requisition/"+accessionId+".pdf";
            byte[] byteArray = googleCloudStorageUtil.readFile(blobName,caseImageBucket);
            responseDto.setResponse(byteArray);
        }
       return responseDto;
    }

    @Override
    public ResponseDto<ReportResponseDto> viewReportPdf(ReportsReqDto reportModelDto) {
        ResponseDto<ReportResponseDto> responseDto = new ResponseDto<>();
        Optional<OrderMessages>  orderMessages = orderMessagesRepository.findById(reportModelDto.getOrderMessageId());
        if(orderMessages.isPresent()){
            String accessionId = orderMessages.get().getAccessionId();
            String blobName = reportModelDto.getLabId()+"/"+accessionId+"/reports_pdf/"+reportModelDto.getCaseId()+"/Pathforce_"+reportModelDto.getCaseId()+".pdf";
            try {
                URL url = googleCloudStorageUtil.generateSignedUrl(caseImageBucket,blobName);
                ReportResponseDto reportResponseDto = new ReportResponseDto();
                reportResponseDto.setSingedUrl(url);
                responseDto.setResponse(reportResponseDto);
                responseDto.setSuccessMsg(SUCCESS_MESSAGE);
                responseDto.setStatusCode(HttpStatus.OK.value());

                //for Audit log
                Optional<UserModel> userModel = usersRepository.findUserModelByEmail(reportModelDto.getEmail());
                CaseDetails caseDetails = caseDetailsRepository.findByCaseId(reportModelDto.getCaseId());
                if(userModel.isPresent()){
                    ActionModel actionModel = auditLogUtil.getActions(AuditAction.REPORT_DOWNLOADED);
                    Object[] args = {accessionId,userModel.get().getEmail()};
                    String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
                    auditLogUtil.saveAuditLogs(AuditAction.REPORT_DOWNLOADED, userModel.get().getId(),
                            caseDetails.getId(),reportModelDto.getLabId(),orderMessages.get().getId(),
                            userModel.get().getEmail(), msg);
                }
            }catch (Exception e){
                log.info("pdf not found");
                log.info(e.getMessage());
                responseDto.setSuccessMsg(SUCCESS_MESSAGE);
                responseDto.setStatusCode(HttpStatus.NO_CONTENT.value());
            }
        }
        return responseDto;

    }

    @Override
    public ResponseDto<ReportResponseDto> saveAnnImage(AnnotatedImageReqDto annotatedImageReqDto) throws Exception {
        ResponseDto<ReportResponseDto> responseDto = new ResponseDto<>();

        ReportResponseDto res = new ReportResponseDto();
        String filtPath = annotatedImageReqDto.getFilePath();
        String base64Url = annotatedImageReqDto.getBase64URL();
        String caseId = annotatedImageReqDto.getCaseId();
        String barCode = annotatedImageReqDto.getBarCodeId();
        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Url);
        String uploadImageLink = googleCloudStorageUtil.uploadFilewithType(imageBytes,caseImageBucket,filtPath,"image/png");
        URL uploadImageURL = googleCloudStorageUtil.generateSignedUrl(caseImageBucket,filtPath);
        res.setSingedUrl(uploadImageURL);
        responseDto.setResponse(res);
        responseDto.setSuccessMsg(SUCCESS_MESSAGE);
        responseDto.setStatusCode(HttpStatus.OK.value());
        try{
            Optional<UserModel> userModel = usersRepository.findUserModelByEmail(annotatedImageReqDto.getEmail());
            log.info("User modal available");
            OrderMessages orderMessages = orderMessagesRepository.findByAccessionId(annotatedImageReqDto.getAccessionId());
            if(userModel.isPresent()){
                log.info("Order Message modal available");
                ActionModel actionModel = auditLogUtil.getActions(AuditAction.ANNOTATED_IMAGE_DOWNLOADED);
                Object[] args = {barCode,userModel.get().getEmail()};
                String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
                log.info("Message::"+msg);
                auditLogUtil.saveAuditLogs(AuditAction.ANNOTATED_IMAGE_DOWNLOADED, userModel.get().getId(),
                        null,orderMessages.getLabDetail().getLabid(),orderMessages.getId(),null,msg);
            }
        }catch (Exception e){
            log.info("Exception occurred for saveAnnImage audit {}",e.getMessage());
        }
        return responseDto;
    }

    public String convertSingedURLToBase64(String strSignedUrl) throws Exception{
        URL imageUrl = new URL(strSignedUrl);
        URLConnection ucon = imageUrl.openConnection();
        InputStream is = ucon.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = is.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, read);
        }
        baos.flush();
        byte[] encode = Base64.encodeBase64(baos.toByteArray(), true);
        return new String(encode);
    }

    /*public LabHeadingsResponseDto convertToLabHeadResponseDto(LabHeadings labHeadings) throws Exception{
        LabHeadingsResponseDto labHeadingsResponseDto = new LabHeadingsResponseDto();
        labHeadingsResponseDto.setId(labHeadings.getId());
        labHeadingsResponseDto.setFirstHeading(labHeadings.getFirstHeading());
        labHeadingsResponseDto.setSecondHeading(labHeadings.getSecondHeading());
        labHeadingsResponseDto.setThirdHeading(labHeadings.getThirdHeading());
        labHeadingsResponseDto.setFourthHeading(labHeadings.getFourthHeading());
        labHeadingsResponseDto.setFifthHeading(labHeadings.getFifthHeading());
        labHeadingsResponseDto.setSixthHeading(labHeadings.getSixthHeading());
        labHeadingsResponseDto.setSeventhHeading(labHeadings.getSeventhHeading());
        return labHeadingsResponseDto;
    }*/

    private UserModelResponseDto convertUserResDto(UserModel user) {
        UserModelResponseDto userModelResDto = new UserModelResponseDto();
        userModelResDto.setId(user.getId());
        userModelResDto.setFirstName(user.getFirstName());
        userModelResDto.setMiddleName(user.getMiddleName());
        userModelResDto.setLastName(user.getLastName());
        userModelResDto.setGender(user.getGender());
        userModelResDto.setNpi(user.getNpi());
        userModelResDto.setStreetAddress(user.getStreetAddress());
        userModelResDto.setSearchAddress(user.getSearchAddress());
        userModelResDto.setCity(user.getCity());
        userModelResDto.setHomeState(user.getHomeState());
        userModelResDto.setZip(user.getZip());
        userModelResDto.setMobilePh(user.getMobilePh());
        userModelResDto.setHomePh(user.getHomePh());
        userModelResDto.setEmergencyPh(user.getEmergencyPh());
        userModelResDto.setEmail(user.getEmail());
        userModelResDto.setDegree(user.getDegree());
        userModelResDto.setCollege(user.getCollege());
        userModelResDto.setYearOfPassing(user.getYearOfPassing());
        userModelResDto.setPasswordChangeRequired(user.isPasswordChangeRequired());
        userModelResDto.setActive(user.isActive());
//            userModelResDto.setAssociatedLab(user.getLabDetails());
        userModelResDto.setProfileImg(user.getProfileImg());


        List<DesignationModel> designation=user.getDesignation();
        List<DesignationResponseDto> lstDesigResDto=new ArrayList<DesignationResponseDto>();
        if(!designation.isEmpty()){
            for(DesignationModel desig: designation){
                DesignationResponseDto designationResDto = new DesignationResponseDto();
                designationResDto.setId(desig.getId());
                designationResDto.setName(desig.getDesignationName());
                lstDesigResDto.add(designationResDto);
            }
            userModelResDto.setDesignation(lstDesigResDto);
        }

        List<StateModel> states=user.getLicensedStates();
        List<StateResponseDto> lstStateResDto=new ArrayList<StateResponseDto>();
        if(!states.isEmpty()){
            for(StateModel stateM: states){
                StateResponseDto stateResDto=new StateResponseDto();
                stateResDto.setId(stateM.getId());
                stateResDto.setName(stateM.getStateName());
                lstStateResDto.add(stateResDto);
            }
            userModelResDto.setLicensedStates(lstStateResDto);
        }

        List<LabDetail> associatedLabs=user.getLabDetails();
        List<LabResponseDto> lstAssoLabResDto=new ArrayList<LabResponseDto>();
        if(!associatedLabs.isEmpty()){
            for(LabDetail assoLab: associatedLabs){
                LabResponseDto labResponseDto =new LabResponseDto();
                labResponseDto.setLabId(assoLab.getLabid());
                labResponseDto.setLabName(assoLab.getLabName());
                lstAssoLabResDto.add(labResponseDto);
            }
            userModelResDto.setLabDetails(lstAssoLabResDto);
        }

        List<Role> roles = user.getRoles();
        List<RoleResponseDto> lstRolesResDto = new ArrayList<RoleResponseDto>();
        if(!roles.isEmpty()) {
            for (Role role : roles) {
                RoleResponseDto roleResDto = new RoleResponseDto();
                roleResDto.setId(role.getId());
                roleResDto.setRoleName(role.getRoleName());
                lstRolesResDto.add(roleResDto);
            }
            userModelResDto.setRoles(lstRolesResDto);
        }
        return userModelResDto;
    }
    /*public LabResponseDto convertToLabResponseDto(LabDetail labDetail){
        LabResponseDto labResponseDto = new LabResponseDto();
        labResponseDto.setLabId(labDetail.getLabid());
        labResponseDto.setLabName(labDetail.getLabName());
        labResponseDto.setLabEmail(labDetail.getLabEmail());
        labResponseDto.setLabContactNo(labDetail.getLabContactNo());
        labResponseDto.setLabWebsite(labDetail.getLabWebsite());
        labResponseDto.setLabRegistrationDocument(labDetail.getLabRegistrationDocument());
        labResponseDto.setLabRegistrationNo(labDetail.getLabRegistrationNo());

        labResponseDto.setDateCreated(labDetail.getCreatedDate());
        labResponseDto.setCreatedBy(labDetail.getCreatedBy());
        labResponseDto.setLastModifiedDate(labDetail.getLastModifiedDate());
        labResponseDto.setLastModifiedBy(labDetail.getLastModifiedBy());

        labResponseDto.setUserName(labDetail.getUserName());
        return labResponseDto;
    };*/

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
        Optional.ofNullable(orderMessages.getAccessionId())
                .ifPresent(orderMessageDto::setAccessionId);
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

}