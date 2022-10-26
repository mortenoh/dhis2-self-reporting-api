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
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.hisp.dhis.integration.configuration.SelfReportingProperties;
import org.hisp.dhis.integration.domain.EmptyResponse;
import org.hisp.dhis.integration.domain.SelfRegistrationEvent;
import org.hisp.dhis.integration.domain.SelfRegistrationEventDataValue;
import org.hisp.dhis.integration.domain.SelfRegistrationEventWrapper;
import org.hisp.dhis.integration.domain.SelfReportingRequest;
import org.hisp.dhis.integration.domain.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping( "/api/self-reporting/vital-signs" )
@RequiredArgsConstructor
public class SelfReportingController
{
    private final RestTemplate restTemplate;

    private final SelfReportingProperties properties;

    @PostMapping
    public ResponseEntity<EmptyResponse> postSelfReport(@RequestBody SelfReportingRequest request )
    {
        System.err.println( request );

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
                    .eventDate( "2022-10-25" )
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
