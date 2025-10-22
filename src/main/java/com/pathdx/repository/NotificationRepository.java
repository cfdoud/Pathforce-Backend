package com.pathdx.repository;

import com.pathdx.model.NotificationEventModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEventModel,Long> {
    public NotificationEventModel findByEventCode(String eventCode);
}
