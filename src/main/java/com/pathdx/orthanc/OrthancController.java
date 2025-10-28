package com.pathdx.orthanc;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrthancController {
  private final OrthancService svc;
  public OrthancController(OrthancService svc){ this.svc = svc; }

  // --- “cases” listing / drill-down ---
  @GetMapping("/patients") public ResponseEntity<String> patients(){ return svc.listPatients(); }
  @GetMapping("/patients/{id}") public ResponseEntity<String> patient(@PathVariable String id){ return svc.getPatient(id); }
  @GetMapping("/studies/{id}")  public ResponseEntity<String> study(@PathVariable String id){ return svc.getStudy(id); }
  @GetMapping("/series/{id}")   public ResponseEntity<String> series(@PathVariable String id){ return svc.getSeries(id); }
  @GetMapping("/instances/{id}") public ResponseEntity<String> instance(@PathVariable String id){ return svc.getInstance(id); }

  // IIIF manifest for OSD
  @GetMapping("/wsi/iiif/manifest/{seriesId}")
  public ResponseEntity<String> iiif(@PathVariable String seriesId){ return svc.iiifManifest(seriesId); }

  // ROI (instance-level attachment)
  @GetMapping("/instances/{instanceId}/roi")
  public ResponseEntity<String> getRoi(@PathVariable String instanceId){
    try {
      ResponseEntity<String> info = svc.getRoiInfo(instanceId);
      String etag = info.getHeaders().getFirst("ETag"); // Orthanc often returns ETag header on /info
      ResponseEntity<String> data = svc.getRoiData(instanceId);
      HttpHeaders out = new HttpHeaders();
      if (etag != null) out.set("ETag", etag);
      return new ResponseEntity<>(data.getBody(), out, HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.noContent().build(); // no attachment yet
    }
  }

  @PutMapping(value="/instances/{instanceId}/roi", consumes=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> putRoi(@PathVariable String instanceId,
                                     @RequestHeader(value="If-Match", required=false) String ifMatch,
                                     @RequestBody String body){
    ResponseEntity<Void> r = svc.putRoiData(instanceId, body, ifMatch);
    return ResponseEntity.status(r.getStatusCode()).build();
  }
}
