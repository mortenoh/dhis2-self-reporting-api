package org.hisp.dhis.integration.web;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hisp.dhis.integration.configuration.SelfReportingProperties;
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
public class SelfInfoController {

    private final SelfReportingProperties properties;
    private final RestTemplate restTemplate;

    private final Map<String, ValueSetter> valueSetters = new HashMap<>();

    @PostConstruct
    private void fillValueSetters() {
        valueSetters.put(properties.getFirstNameAttribute(), UserInfo::setFirstName);
        valueSetters.put(properties.getLastNameAttribute(), UserInfo::setLastName);
        valueSetters.put(properties.getDobAttribute(), UserInfo::setDob);
    }

    @GetMapping("/{teiId}")
    public ResponseEntity<InfoResponse> getInformation(@PathVariable String teiId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().uri(URI.create(properties.getBaseUrl())).path("/trackedEntityInstances/" + teiId).queryParam("fields", "attributes[attribute,value,displayName]").build().encode();

        try {
            ResponseEntity<TrackedEntityAttributes> responseEntity = restTemplate.getForEntity(uriComponents.toUri(), TrackedEntityAttributes.class);

            // Do mapping
            UserInfo userInfo = new UserInfo();

            for (Attribute attribute : Objects.requireNonNull(responseEntity.getBody()).getAttributes()) {
                if (valueSetters.containsKey(attribute.getAttribute())) {
                    valueSetters.get(attribute.getAttribute()).setValue(userInfo, attribute.getValue());
                }
            }

            InfoResponse infoResponse = InfoResponse.builder().status(Status.OK).info(userInfo).build();

            return ResponseEntity.ok(infoResponse);
        } catch (HttpClientErrorException ex) {
            InfoResponse infoResponse = InfoResponse.builder().status(Status.ERROR).build();
            return ResponseEntity.status(ex.getStatusCode()).body(infoResponse);
        }
    }
}

@Data
@Builder
class InfoResponse {
    private Status status;
    private UserInfo info;
}

interface ValueSetter {
    void setValue(UserInfo userInfo, String value);
}

@Data
class UserInfo {
    private String firstName;
    private String lastName;
    private String dob;
}

@Data
class Attribute {
    private String attribute;
    private String value;
}

@Data
class TrackedEntityAttributes {
    private List<Attribute> attributes = new ArrayList<>();
}
