package com.pathdx.repository;

import com.pathdx.dto.responseDto.UserNotificationResponseDto;
import com.pathdx.model.UserNotificationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotificationModel,Long> {
    public List<UserNotificationModel> findByUserId(Long userId);


    @Query(value="select count(*) from user_notifications\n" +
            "where viewed=false and user_id=?1\n" +
            "group by user_id;", nativeQuery = true)
    Long findNotificationCount(Long userId);
}
