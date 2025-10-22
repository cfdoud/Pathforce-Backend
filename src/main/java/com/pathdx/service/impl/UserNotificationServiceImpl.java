package com.pathdx.service.impl;

import com.pathdx.dto.responseDto.CountUserNotification;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.UserNotificationResponseDto;
import com.pathdx.model.UserNotificationModel;
import com.pathdx.repository.UserNotificationRepository;
import com.pathdx.service.UserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private EntityManager entityManager;

    public ResponseDto<List<UserNotificationResponseDto>> getUserNotification(Long userId) {
        ResponseDto<List<UserNotificationResponseDto>> responseDto = new ResponseDto<>();
       /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObj = null;
        try {
            dateObj = sdf.parse(sdf.format(new Date()));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        LocalDate currentDate = LocalDate.parse(dateObj.toString());
        LocalDate previousDate = currentDate.minusDays(30);*/

        long today = System.currentTimeMillis();
        long nDays = 30 * 24 * 60 * 60 * 1000;
        long nDaysAgo = today - nDays;
        Date nDaysAgoDate = new Date( nDaysAgo );


        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserNotificationModel> criteriaQuery = builder.createQuery(UserNotificationModel.class);
        Root<UserNotificationModel> root = criteriaQuery.from(UserNotificationModel.class);
        Predicate predicate = builder.conjunction();
        predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("createDate"), nDaysAgoDate));

        criteriaQuery.where(builder.and(predicate));
        criteriaQuery.where(builder.equal(root.get("userId"), userId));
        criteriaQuery.orderBy(builder.desc(root.get("id")));
        TypedQuery<UserNotificationModel> query = entityManager.createQuery(criteriaQuery);
        List<UserNotificationModel> userNotificationModels = query.getResultList();
        List<UserNotificationModel> userNotificationModelList = new ArrayList<UserNotificationModel>();
        for(UserNotificationModel userNotificationModel : userNotificationModels) {
            userNotificationModel.setViewed(true);
            UserNotificationModel userNotificationModel1 = userNotificationRepository.save(userNotificationModel);
            userNotificationModelList.add(userNotificationModel1);
        }
        List<UserNotificationResponseDto> lstUserNotificationResDto = convertModelToDto(userNotificationModelList);
        responseDto.setResponse(lstUserNotificationResDto);
        return responseDto;
    }

    @Override
    public ResponseDto<CountUserNotification> getNotificationCount(Long userId){
        ResponseDto<CountUserNotification> responseDto = new ResponseDto<>();
        CountUserNotification countUserNotification = new CountUserNotification();
        Long count =  userNotificationRepository.findNotificationCount(userId);
        if(count == null)
            countUserNotification.setNotificationCount(0L);
        else
            countUserNotification.setNotificationCount(count);

        responseDto.setResponse(countUserNotification);
        return responseDto;
    }

    @Override
    public ResponseDto<List<UserNotificationResponseDto>> updateUserNotification(Long userNotificationId) {
        ResponseDto<List<UserNotificationResponseDto>> responseDto = new ResponseDto<>();
        Optional<UserNotificationModel> userNotificationModelOptional = userNotificationRepository.findById(userNotificationId);
        List<UserNotificationModel> userNotificationModelList = new ArrayList<UserNotificationModel>();
        if(userNotificationModelOptional.isPresent()) {
            UserNotificationModel userNotificationModel = userNotificationModelOptional.get();
            userNotificationModel.setViewed(true);
            UserNotificationModel userNotificationModel1=userNotificationRepository.save(userNotificationModel);
            userNotificationModelList.add(userNotificationModel1);
        }
        List<UserNotificationResponseDto> lstUserNotificationResDto = convertModelToDto(userNotificationModelList);
        responseDto.setResponse(lstUserNotificationResDto);
        return responseDto;
    }

    private List<UserNotificationResponseDto> convertModelToDto(List<UserNotificationModel> lstUserNotificationModel) {
        List<UserNotificationResponseDto> lstUserNotificationResDto = new ArrayList<UserNotificationResponseDto>();
        for(UserNotificationModel userNotificationModel : lstUserNotificationModel) {
            UserNotificationResponseDto userNotificationResponseDto = new UserNotificationResponseDto();
            userNotificationResponseDto.setId(userNotificationModel.getId());
            userNotificationResponseDto.setNotificationEventId(userNotificationModel.getNotificationEventId());
            userNotificationResponseDto.setUserId(userNotificationModel.getUserId());
            userNotificationResponseDto.setCreateDate(userNotificationModel.getCreateDate());
            userNotificationResponseDto.setMessage(userNotificationModel.getMessage());
            userNotificationResponseDto.setViewed(userNotificationModel.isViewed());
            lstUserNotificationResDto.add(userNotificationResponseDto);
        }
        return lstUserNotificationResDto;
    }

}
