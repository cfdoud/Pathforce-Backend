package com.pathdx.service.impl;

import com.pathdx.constant.AuditAction;
import com.pathdx.dto.requestDto.EmailModelDto;
import com.pathdx.dto.requestDto.SlideDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.*;
import com.pathdx.repository.*;
import com.pathdx.service.StainsService;
import com.pathdx.service.UserService;
import com.pathdx.utils.AuditLogUtil;
import com.pathdx.utils.CommonUtil;
import com.pathdx.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pathdx.constant.Constants.ADDITION_REQUEST_BODY_Start;
import static com.pathdx.constant.UtilConstants.*;
import static com.pathdx.constant.UtilConstants.ADDITIONAL_REQUEST_THANK;

@Component
@Slf4j
public class StainsServiceImpl implements StainsService {

    @Autowired
    private StainRepository stainRepository;

    @Autowired
    private StainPanelRepository stainPanelRepository;

    @Autowired
    private SlideStainRepository slideStainRepository;

    @Autowired
    private SlideStainPanelRepository slideStainPanelRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Value("${reassignEmail}")
    private String reassignEmailto;

    @Autowired
    private AuditLogUtil auditLogUtil;

    @Autowired
    private OrderMessagesRepository orderMessagesRepository;

    @Override
    public ResponseDto<StainsResponseDto> getAllStains() {
        ResponseDto<StainsResponseDto> responseDto = new ResponseDto<>();
        List<Stains> stains = stainRepository.findAll();
        List<StainPanelModel> stainPanelModels = stainPanelRepository.findAll();
        StainsResponseDto stainsResponseDto = convertModelToDto(stains, stainPanelModels);
        responseDto.setResponse(stainsResponseDto);
        return responseDto;
    }

    private StainsResponseDto convertModelToDto(List<Stains> lstStains, List<StainPanelModel> lstStainPanel) {
        List<StainsDto> lstStainsDto = new ArrayList<StainsDto>();
        List<StainsPanelDto> lstStainPanelDto = new ArrayList<StainsPanelDto>();
        StainsResponseDto responseDto = new StainsResponseDto();
        for(Stains stains : lstStains) {
            StainsDto stainsDto = new StainsDto();
            stainsDto.setId(stains.getId());
            stainsDto.setName(stains.getName());
            stainsDto.setStainType(stains.getStainType());
            stainsDto.setAbbr(stains.getAbbr());
            stainsDto.setCptCode(stains.getCptCode());
            stainsDto.setQuantity(stains.getQuantity());
            lstStainsDto.add(stainsDto);
        }
        for(StainPanelModel stainPanelModel : lstStainPanel) {
            StainsPanelDto stainsPanelDto = new StainsPanelDto();
            stainsPanelDto.setId(stainPanelModel.getId());
            stainsPanelDto.setStainType(stainPanelModel.getStainType());
            lstStainPanelDto.add(stainsPanelDto);
        }
        responseDto.setStains(lstStainsDto);
        responseDto.setStainsPanels(lstStainPanelDto);
        return responseDto;
    }

    @Override
    public ResponseDto<SlideResDto> saveStainsAndStainPanel(SlideDto slideDto, ResponseDto<SlideResDto> slideResDto, String email, String accessionId) {
        SlideResDto resDto = slideResDto.getResponse();
        List<SlideDetails> lstSlideDetail = slideDto.getSlideDetails();
        List<SlideStainsModel> lstSlideStainsModel = slideDto.getSlideStainsModels();
        List<SlideStainPanelModel> lstSlideStainPanelModel = slideDto.getSlideStainPanelModels();
        List<SlideStainsModel> listOfSlideStainModel = new ArrayList<SlideStainsModel>();
        List<SlideStainPanelModel> listOfSlideStainPanelModel = new ArrayList<SlideStainPanelModel>();
        for(SlideDetails slideDetails : lstSlideDetail) {
            for (SlideStainsModel slideStainsModel : lstSlideStainsModel) {
                SlideStainsModel slideStainsModel1 = slideStainRepository.findBySlideId(slideStainsModel.getSlideId());
                if (slideStainsModel1 != null) {
                    slideStainRepository.delete(slideStainsModel1);
                }
                if(slideDetails.getId() == slideStainsModel.getSlideId()) {
                    slideStainsModel.setComment(slideDetails.getComment());
                    SlideStainsModel slideStains = slideStainRepository.save(slideStainsModel);
                    listOfSlideStainModel.add(slideStains);
                }
            }
        }
        List<StainsDto> stainsDtos = convertStainModelToDto(listOfSlideStainModel);
        for(SlideStainPanelModel slideStainPanelModel : lstSlideStainPanelModel) {
            SlideStainPanelModel slideStainPanelModel1 = slideStainPanelRepository.findBySlideId(slideStainPanelModel.getSlideId());
            if (slideStainPanelModel1 != null) {
                slideStainPanelRepository.delete(slideStainPanelModel1);
            }
            SlideStainPanelModel slideStainPanel = slideStainPanelRepository.save(slideStainPanelModel);
            listOfSlideStainPanelModel.add(slideStainPanel);
        }
        List<StainsPanelDto> stainsPanelDto = convertStainPanelModelToDto(listOfSlideStainPanelModel);

        resDto.setStainsDto(stainsDtos);
        resDto.setStainsPanelDto(stainsPanelDto);

        slideResDto.setResponse(resDto);
        sendingEmail(slideResDto, email, accessionId);
        //for Audit log
        for(SlideDetailsDto slideDetailsDto : slideResDto.getResponse().getSlideDetailsDtos()) {
            String barCodeId = slideDetailsDto.getBarcodeId();
            ActionModel actionModel = auditLogUtil.getActions(AuditAction.ADDITIONAL_REQUEST_SUBMITTED);
            Object[] args = {accessionId, barCodeId, email};
            String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(), args);
            Optional<UserModel> user = usersRepository.findUserModelByEmail(email);
            Long userId = user.get().getId();
            OrderMessages orderMessages = orderMessagesRepository.findByAccessionId(accessionId);
            auditLogUtil.saveAuditLogs(AuditAction.ADDITIONAL_REQUEST_SUBMITTED, userId, null, orderMessages.getLabDetail().getLabid(), orderMessages.getId(), null, msg);
        }
        return slideResDto;
    }

    private List<StainsDto> convertStainModelToDto(List<SlideStainsModel> slideStainsModels) {
        List<StainsDto> lstStainDto = new ArrayList<StainsDto>();
        for(SlideStainsModel slideStainsModel : slideStainsModels) {
            String stainId = slideStainsModel.getStainId();
            String[] res = stainId.split("[,]", 0);
            for(String id: res) {
                Optional<Stains> stains = stainRepository.findById(Long.parseLong(id));
                if(stains.isPresent()) {
                    StainsDto stainsDto = new StainsDto();
                    Stains tempStain = stains.get();
                    stainsDto.setId(tempStain.getId());
                    stainsDto.setSlideId(slideStainsModel.getSlideId());
                    stainsDto.setName(tempStain.getName());
                    stainsDto.setAbbr(tempStain.getAbbr());
                    stainsDto.setStainType(tempStain.getStainType());
                    stainsDto.setCptCode(tempStain.getCptCode());
                    stainsDto.setQuantity(tempStain.getQuantity());
                    stainsDto.setComments(slideStainsModel.getComment());
                    lstStainDto.add(stainsDto);
                }
            }

        }
        return  lstStainDto;
    }

    private List<StainsPanelDto> convertStainPanelModelToDto(List<SlideStainPanelModel> lstStainPanelModel) {
        List<StainsPanelDto> lstStainPanelDto = new ArrayList<StainsPanelDto>();
        for(SlideStainPanelModel stainsPanelModel : lstStainPanelModel) {
            StainsPanelDto stainsPanelDto1 = new StainsPanelDto();
            stainsPanelDto1.setId(stainsPanelModel.getId());
            stainsPanelDto1.setStainType(stainsPanelModel.getStainType());
            lstStainPanelDto.add(stainsPanelDto1);
        }
        return  lstStainPanelDto;
    }

    private void sendingEmail(ResponseDto<SlideResDto> resSlideDto, String email, String accessionId) {
        SlideResDto slideResDto = resSlideDto.getResponse();
        Optional<UserModel> user = usersRepository.findUserModelByEmail(email);
        String name = null;
        if(user.isPresent()) {
            name = commonUtil.capitalizeFirstCharacter(user.get().getFirstName()) + " " + commonUtil.capitalizeFirstCharacter(user.get().getLastName());
        }
        for(SlideDetailsDto slideDetailsDto : slideResDto.getSlideDetailsDtos()) {
            List<String> stainType = new ArrayList<String>();
            List<String> abbr = new ArrayList<String>();
            String barCodeId = slideDetailsDto.getBarcodeId();
            String blockId = slideDetailsDto.getBlockId();
            String comments = "";
            for(StainsDto stainsDto : slideResDto.getStainsDto()){
                if(slideDetailsDto.getId() == stainsDto.getSlideId()) {
                    comments = stainsDto.getComments();
                    stainType.add(stainsDto.getStainType());
                    abbr.add(stainsDto.getAbbr());
                }
            }
            String stainTypeStr = formatString(stainType.stream().distinct().collect(Collectors.toList()));
            String abbrStr = formatString(abbr);
            EmailModelDto emailModelDto=new EmailModelDto();
            emailModelDto.setFrom("info@pathforcetech.com");
            emailModelDto.setTo(reassignEmailto);
            emailModelDto.setSubject(ADDITIONAL_REQUEST_SUBJECT);
            String msgBody = ADDITION_REQUEST_BODY_Start+" "+ ADDITIONAL_REQUEST_BODY+ " "+ name + " " + ADDITIONAL_REQUEST_BODY2 +
                    "<br><ul style='list-style-type:circle'><li>"+
                    "Accession ID:" + accessionId +
                    "</li><br><li>"+
                    "Barcode/Slide ID:\n" + barCodeId +
                    "</li><br><li>"+
                    "Block ID:\n" + blockId +
                    "</li><br><li>"+
                    "Stain Type:\n" + stainTypeStr +
                    "</li><br><li>"+
                    "Abbreviation:\n" + abbrStr+
                    "</li><br><li>" +
                    "comments:\n" + comments+
                    "</li></ul><br>" +
                    ADDITIONAL_REQUEST_THANK;
            emailModelDto.setBody(msgBody);
            try {
                emailUtil.sendmail(emailModelDto);
            } catch (MessagingException e) {
                resSlideDto.setError(e.getMessage());

            } catch (IOException e) {
                resSlideDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                resSlideDto.setError(e.getMessage());
            }
        }
    }

    private String formatString(List<String> lstString) {
        String result = String.join(",", lstString);
        return result;
    }
}
