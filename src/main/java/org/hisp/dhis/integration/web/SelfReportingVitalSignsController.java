/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.integration.web;

import java.net.URI;
import java.util.*;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.hisp.dhis.integration.configuration.SelfReportingProperties;
import org.hisp.dhis.integration.domain.*;
import org.hisp.dhis.integration.util.ValueSetter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping( "/api/self-reporting/vital-signs" )
@RequiredArgsConstructor
public class SelfReportingVitalSignsController
{
    private final RestTemplate restTemplate;

    private final SelfReportingProperties properties;

    private final Map<String, ValueSetter<SelfReportingPayload>> dataElementsSetters = new HashMap<>();

    @PostConstruct
    private void fillValueSetters()
    {
        dataElementsSetters.put( "Nbwya6fr9Do",
            ( selfReportingPayload, value ) -> selfReportingPayload.setDiastolic( Integer.valueOf( value ) ) );
        dataElementsSetters.put( "mKLWtg9zlZF",
            ( selfReportingPayload, value ) -> selfReportingPayload.setSystolic( Integer.valueOf( value ) ) );
        dataElementsSetters.put( "VnOTAxhekAb",
            ( selfReportingPayload, value ) -> selfReportingPayload.setPulse( Integer.valueOf( value ) ) );
        dataElementsSetters.put( "xHb2fLCu4iZ",
            ( selfReportingPayload, value ) -> selfReportingPayload.setWeight( Double.valueOf( value ) ) );
    }

    @GetMapping( "/{id}" )
    public ResponseEntity<VitalsHistoryResponse> getSelfReports( @PathVariable String id )
    {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .uri( URI.create( properties.getBaseUrl() ) ).path( "/api/events" )
            .queryParam( "program", properties.getProgramId() )
            .queryParam( "trackedEntityInstance", id ).build().encode();

        try
        {
            ResponseEntity<SelfRegistrationEventWrapper> forEntity = restTemplate.getForEntity( uriComponents.toUri(),
                SelfRegistrationEventWrapper.class );

            List<SelfReportingPayload> vitalsHistory = new ArrayList<>();
            for ( SelfRegistrationEvent event : Objects.requireNonNull( forEntity.getBody() ).getEvents() )
            {
                SelfReportingPayload selfReportingPayload = new SelfReportingPayload();
                selfReportingPayload.setId( id );
                for ( SelfRegistrationEventDataValue dataValue : event.getDataValues() )
                {
                    if ( dataElementsSetters.containsKey( dataValue.getDataElement() ) )
                    {
                        dataElementsSetters.get( dataValue.getDataElement() ).setValue( selfReportingPayload,
                            dataValue.getValue() );
                    }
                }
                vitalsHistory.add( selfReportingPayload );
            }

            VitalsHistoryResponse vitalsHistoryResponse = VitalsHistoryResponse.builder().status( Status.OK )
                .vitals( vitalsHistory ).build();
            return ResponseEntity.ok( vitalsHistoryResponse );
        }
        catch ( HttpClientErrorException ex )
        {
            VitalsHistoryResponse vitalsHistoryResponse = VitalsHistoryResponse.builder().status( Status.ERROR )
                .build();
            return ResponseEntity.status( ex.getStatusCode() ).body( vitalsHistoryResponse );
        }
    }

    @PostMapping
    public ResponseEntity<EmptyResponse> postSelfReport( @RequestBody SelfReportingPayload request )
    {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .uri( URI.create( properties.getBaseUrl() ) )
            .path( "/api/events" )
            .build()
            .encode();

        SelfRegistrationEventWrapper payload = SelfRegistrationEventWrapper.builder()
            .events( List.of(
                SelfRegistrationEvent.builder()
                    .trackedEntityInstance( request.getId() )
                    .program( properties.getProgramId() )
                    .programStage( "tsYXkFNGPX4" )
                    .orgUnit( "ImspTQPwCqd" )
                    .status( "COMPLETED" )
                    .enrollment( "WXHbktGX1A9" )
                    .eventDate( request.getDate() )
                    .dataValues( List.of(
                        SelfRegistrationEventDataValue.builder().dataElement( "mKLWtg9zlZF" )
                            .value( request.getSystolic().toString() ).build(),
                        SelfRegistrationEventDataValue.builder().dataElement( "Nbwya6fr9Do" )
                            .value( request.getDiastolic().toString() ).build(),
                        SelfRegistrationEventDataValue.builder().dataElement( "VnOTAxhekAb" )
                            .value( request.getPulse().toString() ).build(),
                        SelfRegistrationEventDataValue.builder().dataElement( "xHb2fLCu4iZ" )
                            .value( request.getWeight().toString() ).build() ) )
                    .build() ) )
            .build();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity( uriComponents.toUri(), payload,
            String.class );

        if ( !responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null )
        {
            EmptyResponse response = EmptyResponse.builder()
                .status( Status.ERROR )
                .build();

            return ResponseEntity.ok( response );
        }

        EmptyResponse response = EmptyResponse.builder()
            .status( Status.OK )
            .build();

        return ResponseEntity.ok( response );
    }
}
