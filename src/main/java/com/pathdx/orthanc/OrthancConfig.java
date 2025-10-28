package com.pathdx.orthanc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableConfigurationProperties(OrthancProps.class)
public class OrthancConfig {

  @Bean
  RestTemplate orthancRestTemplate(OrthancProps p) {
    RestTemplate rt = new RestTemplate();
    String basic = Base64.getEncoder()
        .encodeToString((p.getUser() + ":" + p.getPass()).getBytes(StandardCharsets.UTF_8));
    ClientHttpRequestInterceptor auth = (req, body, ex) -> {
      req.getHeaders().add("Authorization", "Basic " + basic);
      return ex.execute(req, body);
    };
    rt.setInterceptors(List.of(auth));
    return rt;
  }
}

@ConfigurationProperties(prefix = "orthanc")
class OrthancProps {
  private String url;
  private String user;
  private String pass;
  private String attachmentName = "roiJson";

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  public String getUser() { return user; }
  public void setUser(String user) { this.user = user; }
  public String getPass() { return pass; }
  public void setPass(String pass) { this.pass = pass; }
  public String getAttachmentName() { return attachmentName; }
  public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
}
