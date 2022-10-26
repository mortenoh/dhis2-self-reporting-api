package org.hisp.dhis.integration.web;

import lombok.RequiredArgsConstructor;
import org.hisp.dhis.integration.configuration.SelfReportingProperties;
import org.hisp.dhis.integration.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/self-reporting/info")
@RequiredArgsConstructor
public class PatientInfoController {

    private final SelfReportingProperties properties;
    private final RestTemplate restTemplate;

    private final Map<String, ValueSetter> valueSetters = new HashMap<>();

    @PostConstruct
    private void fillValueSetters() {
        valueSetters.put(properties.getFirstNameAttribute(), PatientInfo::setFirstName);
        valueSetters.put(properties.getLastNameAttribute(), PatientInfo::setLastName);
        valueSetters.put(properties.getDobAttribute(), PatientInfo::setDob);
    }

    @GetMapping("/{teiId}")
    public ResponseEntity<PatientInfoResponse> getInformation(@PathVariable String teiId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().uri(URI.create(properties.getBaseUrl())).path("/trackedEntityInstances/" + teiId).queryParam("fields", "attributes[attribute,value,displayName]").build().encode();

        try {
            ResponseEntity<TrackedEntityAttributes> responseEntity = restTemplate.getForEntity(uriComponents.toUri(), TrackedEntityAttributes.class);

            // Do mapping
            PatientInfo patientInfo = new PatientInfo();

            for (Attribute attribute : Objects.requireNonNull(responseEntity.getBody()).getAttributes()) {
                if (valueSetters.containsKey(attribute.getAttribute())) {
                    valueSetters.get(attribute.getAttribute()).setValue(patientInfo, attribute.getValue());
                }
            }

            PatientInfoResponse patientInfoResponse = PatientInfoResponse.builder().status(Status.OK).info(patientInfo).build();

            return ResponseEntity.ok(patientInfoResponse);
        } catch (HttpClientErrorException ex) {
            PatientInfoResponse patientInfoResponse = PatientInfoResponse.builder().status(Status.ERROR).build();
            return ResponseEntity.status(ex.getStatusCode()).body(patientInfoResponse);
        }
    }
}

interface ValueSetter {
    void setValue(PatientInfo patientInfo, String value);
}

