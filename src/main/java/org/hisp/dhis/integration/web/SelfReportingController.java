package org.hisp.dhis.integration.web;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hisp.dhis.integration.configuration.SelfReportingProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/api/self-reporting" )
@RequiredArgsConstructor
public class SelfReportingController
{
    private final RestTemplate restTemplate;
    private final SelfReportingProperties properties;

    @PostMapping
    public ResponseEntity<ReportingResponse> postSelfReport()
    {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .uri( URI.create( properties.getBaseUrl() ) )
            .path( "/api/dataElements" )
            .queryParam( "fields", "id,displayName" )
            .queryParam( "paging", false )
            .build()
            .encode();

        ResponseEntity<DataElementWrapper> responseEntity = restTemplate.getForEntity( uriComponents.toUri(),
            DataElementWrapper.class );

        if ( !responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null )
        {
            ReportingResponse reportingResponse = ReportingResponse.builder()
                .status( Status.ERROR )
                .build();

            return ResponseEntity.ok(reportingResponse);
        }

        ReportingResponse reportingResponse = ReportingResponse.builder()
            .status( Status.OK )
            .dataElements( responseEntity.getBody().getDataElements() )
            .build();

        return ResponseEntity.ok(reportingResponse);
    }
}

@Data
@Builder
class ReportingResponse
{
    private Status status;
    private List<DataElement> dataElements;
}

@Data
class DataElementWrapper
{
    private List<DataElement> dataElements = new ArrayList<>();
}

@Data
class DataElement
{
    private String id;
    private String displayName;
}