package com.pathdx.repository;

import com.pathdx.model.NotificationEventsApplicableRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventApplicableRoleRepository extends JpaRepository<NotificationEventsApplicableRoleModel, Long> {
    public List<NotificationEventsApplicableRoleModel> findByRoleId(Long roleId);
}
