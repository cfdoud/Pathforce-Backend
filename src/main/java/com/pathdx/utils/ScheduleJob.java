package com.pathdx.utils;

import com.pathdx.service.CaseAllocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduleJob {
    /*everyday at 23.50
    @Scheduled(cron ="0 50 23 * * *")
    every 5 sec
    @Scheduled(fixedRate = 5000)*/
    @Autowired
    CaseAllocationService caseAllocationService;

    @Value("${RULE_ALLOCATION_FLAG}")
    private  boolean flag;

    @Scheduled(cron ="0 50 23 * * *")
    /*@Scheduled(fixedRate = 600000)*/
    public void triggerJob(){
        if(flag) {
            log.info("Scheduler job started");
            caseAllocationService.casesAllocation();
            caseAllocationService.sendEmialForPendingCases();
            log.info("Scheduler job Ended");
        }else{
            log.info("Scheduler job flag disabled");
        }
    }
}
