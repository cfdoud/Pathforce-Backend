package com.pathdx.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ActivityLogFactory implements InitializingBean {

    private Map<String, String> map = new HashMap<>();

    @Override
    public void afterPropertiesSet()
            throws Exception {

        //User Activity Map
        map.put("save","Sign Up Successful");
        map.put("forgotpassword","Forgot Password");

    }

    public String getMethodDescription(String methodName) {
        String description = map.get(methodName);
        if (description == null) {
        //    log.error("Invalid method Name to perform operation : Service Type : " + methodName);
        }
        return description;
    }

}
