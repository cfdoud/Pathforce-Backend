package com.pathdx.repository;

import com.pathdx.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

}
