package com.pathdx.repository.reposervice;

import com.pathdx.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogRepoService {

    @Autowired
    private ActivityLogRepository activityLogRepository;


}
