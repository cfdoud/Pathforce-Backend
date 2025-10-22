package com.pathdx.utils;

import com.pathdx.dto.adsync.AdUserInfo;
import com.pathdx.dto.adsync.AzureAdToken;
import com.pathdx.dto.adsync.Value;
import com.pathdx.model.LabDetail;
import com.pathdx.model.Role;
import com.pathdx.model.UserModel;
import com.pathdx.repository.LabDetailRepository;
import com.pathdx.repository.RoleRepository;
import com.pathdx.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SamlAuthUtil {
    @Autowired
    UsersRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    LabDetailRepository labDetailRepository;

    @org.springframework.beans.factory.annotation.Value("${azure.grant.type}")
    String grantType;

    @org.springframework.beans.factory.annotation.Value("${azure.client.id}")
    String clientId;
    @org.springframework.beans.factory.annotation.Value("${azure.client.secret}")
    String clientSecret;
    @org.springframework.beans.factory.annotation.Value("${azure.resource}")
    String resource;
    @org.springframework.beans.factory.annotation.Value("${azure.user.list.url}")
    String userDetailsUrl;

    @org.springframework.beans.factory.annotation.Value("${azure.token.url}")
    String tokenUrl;

    @org.springframework.beans.factory.annotation.Value("${azure.token.me.url}")
    String selfUrl;

    @org.springframework.beans.factory.annotation.Value("${USER_SYNC_ON}")
    Boolean USER_SYNC_ON;

    @org.springframework.beans.factory.annotation.Value("${LAB_ID}")
    String labId;

    public boolean validateToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Value> adUser = restTemplate.exchange(selfUrl, HttpMethod.GET, request, Value.class);
        if(adUser.hasBody()){
            Value value = adUser.getBody();
            if(value.getMail()!=null){
                Optional<UserModel> userModel = userRepository.findUserModelByEmail(value.getMail());
                if(!userModel.isPresent()){
                    return false;
                }
            }
        }else{
            return false;
        }

        return true;
    }

    @Scheduled(fixedDelay = 1000*60)
    //@Scheduled(cron ="0 50 23 * * *")
    @Transactional
    public void azureAdUserSync() {
        if(USER_SYNC_ON) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //mobileNumber = code + mobileNumber;
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", grantType);
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("resource", resource);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            RestTemplate restTemplate = new RestTemplate();


            try {
                ResponseEntity<AzureAdToken> azureAdTokenEntity = restTemplate.postForEntity(tokenUrl, request, AzureAdToken.class);
                if (azureAdTokenEntity.hasBody()) {
                    AzureAdToken azureAdToken = azureAdTokenEntity.getBody();
                    getUsers(azureAdToken.getAccessToken(), userDetailsUrl);
                } else {
                    log.info("No token found for AD Sync");
                }


            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private AdUserInfo getUsers(String accessToken, String url) {
        AdUserInfo adUserInfo = new AdUserInfo();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AdUserInfo> adUsers = restTemplate.exchange(url, HttpMethod.GET, request, AdUserInfo.class);
        saveUserInDb(adUsers.getBody().getValue());
        if (adUsers.getBody().getNextLink() != null) {
            getUsers(accessToken, adUsers.getBody().getNextLink()).getValue();
        }
        return adUserInfo;

    }

    private void saveUserInDb(List<Value> values) {
        //update into DB
        List<UserModel> userModels = new ArrayList<>();
        if (values.size() > 0) {
            for (Value value : values) {
                if (value.getMail() != null) {
                    Optional<UserModel> userModelOptional = userRepository.findUserModelByEmail(value.getMail());
                    if (userModelOptional.isPresent()) {
                        log.info("User Already Existed");
                    } else {
                        UserModel userModel = new UserModel();
                        userModel.setActive(true);
                        userModel.setPasswordChangeRequired(false);
                        userModel.setEmail(value.getMail());
                        userModel.setFirstName(value.getDisplayName());
                        List<Role> roles = new ArrayList<>();
                        Optional<Role> role = roleRepository.findById(1L);
                        roles.add(role.get());
                        Optional<LabDetail> labDetail = labDetailRepository.findByLabid(labId);
                        List<LabDetail> labDetails = new ArrayList<>();
                        labDetails.add(labDetail.get());
                        userModel.setLabDetails(labDetails);
                        userModel.setRoles(roles);
                        userModels.add(userModel);
                    }

                } else {
                    log.info("Email is null");
                }


            }
            if (userModels.size() > 0) {
                userRepository.saveAll(userModels);
            }

        } else {
            log.info("### No Added User Found ###");
        }
    }


}
