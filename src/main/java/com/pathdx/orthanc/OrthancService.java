package com.pathdx.orthanc;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrthancService {
  private final RestTemplate rt;
  private final OrthancProps props;

  public OrthancService(RestTemplate orthancRestTemplate, OrthancProps props) {
    this.rt = orthancRestTemplate;
    this.props = props;
  }

  private String u(String path) { // build URL
    return props.getUrl() + path;
  }

  // --- Basic REST pass-throughs (return raw JSON as String) ---
  public ResponseEntity<String> listPatients() {
    return rt.getForEntity(u("/patients"), String.class);
  }
  public ResponseEntity<String> getPatient(String id) {
    return rt.getForEntity(u("/patients/{id}"), String.class, id);
  }
  public ResponseEntity<String> getStudy(String id) {
    return rt.getForEntity(u("/studies/{id}"), String.class, id);
  }
  public ResponseEntity<String> getSeries(String id) {
    return rt.getForEntity(u("/series/{id}"), String.class, id);
  }
  public ResponseEntity<String> getInstance(String id) {
    return rt.getForEntity(u("/instances/{id}"), String.class, id);
  }

  // IIIF manifest
  public ResponseEntity<String> iiifManifest(String seriesId) {
    return rt.getForEntity(u("/wsi/iiif/manifest/{sid}"), String.class, seriesId);
  }

  // ROI attachment helpers
  public ResponseEntity<String> getRoiInfo(String instanceId) {
    String name = props.getAttachmentName();
    return rt.getForEntity(u("/instances/{id}/attachments/{name}/info"), String.class, instanceId, name);
  }

  public ResponseEntity<String> getRoiData(String instanceId) {
    String name = props.getAttachmentName();
    return rt.getForEntity(u("/instances/{id}/attachments/{name}/data"), String.class, instanceId, name);
  }

  public ResponseEntity<Void> putRoiData(String instanceId, String body, String ifMatch) {
    String name = props.getAttachmentName();
    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);
    if (ifMatch != null && !ifMatch.isBlank()) h.set("If-Match", ifMatch);
    HttpEntity<String> entity = new HttpEntity<>(body, h);
    return rt.exchange(u("/instances/{id}/attachments/{name}"),
        HttpMethod.PUT, entity, Void.class, instanceId, name);
  }
}
