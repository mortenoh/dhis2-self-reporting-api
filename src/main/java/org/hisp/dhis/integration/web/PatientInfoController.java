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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping( "/api/self-reporting/info" )
@RequiredArgsConstructor
public class PatientInfoController
{

    private final SelfReportingProperties properties;

    private final RestTemplate restTemplate;

    private final Map<String, ValueSetter> valueSetters = new HashMap<>();

    @PostConstruct
    private void fillValueSetters()
    {
        valueSetters.put( properties.getFirstNameAttribute(), PatientInfo::setFirstName );
        valueSetters.put( properties.getLastNameAttribute(), PatientInfo::setLastName );
        valueSetters.put( properties.getDobAttribute(), PatientInfo::setDob );
    }

    @GetMapping( "/{id}" )
    public ResponseEntity<PatientInfoResponse> getInformation( @PathVariable String id )
    {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().uri( URI.create( properties.getBaseUrl() ) )
            .path( "/trackedEntityInstances/" + id )
            .queryParam( "fields", "attributes[attribute,value,displayName]" ).build().encode();

        try
        {
            ResponseEntity<TrackedEntityAttributes> responseEntity = restTemplate.getForEntity( uriComponents.toUri(),
                TrackedEntityAttributes.class );

            // Do mapping
            PatientInfo patientInfo = new PatientInfo();

            for ( Attribute attribute : Objects.requireNonNull( responseEntity.getBody() ).getAttributes() )
            {
                if ( valueSetters.containsKey( attribute.getAttribute() ) )
                {
                    valueSetters.get( attribute.getAttribute() ).setValue( patientInfo, attribute.getValue() );
                }
            }

            PatientInfoResponse patientInfoResponse = PatientInfoResponse.builder().status( Status.OK )
                .info( patientInfo ).build();

            return ResponseEntity.ok( patientInfoResponse );
        }
        catch ( HttpClientErrorException ex )
        {
            PatientInfoResponse patientInfoResponse = PatientInfoResponse.builder().status( Status.ERROR ).build();
            return ResponseEntity.status( ex.getStatusCode() ).body( patientInfoResponse );
        }
    }
}

interface ValueSetter
{
    void setValue( PatientInfo patientInfo, String value );
}
