package com.pathdx.repository;

import com.pathdx.model.UserNotificationSubscriptionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationSubscriptionRepository extends JpaRepository<UserNotificationSubscriptionModel, Long> {

    public List<UserNotificationSubscriptionModel> findByUserId(Long userId);
    public List<UserNotificationSubscriptionModel> findByNotificationEventId(Long notificationId);
}
