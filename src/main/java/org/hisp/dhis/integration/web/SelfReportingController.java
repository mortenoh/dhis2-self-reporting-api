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
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.hisp.dhis.integration.configuration.SelfReportingProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping( "/api/self-reporting" )
@RequiredArgsConstructor
public class SelfReportingController
{
    private final RestTemplate restTemplate;

    private final SelfReportingProperties properties;

    @PostMapping
    public ResponseEntity<Response> postSelfReport( @RequestBody SelfReportingRequest request )
    {
        System.err.println( request );

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
            Response response = Response.builder()
                .status( Status.ERROR )
                .build();

            return ResponseEntity.ok( response );
        }

        Response response = Response.builder()
            .status( Status.OK )
            .dataElements( responseEntity.getBody().getDataElements() )
            .build();

        return ResponseEntity.ok( response );
    }
}

@Data
@Builder
class Response
{
    private Status status;

    private List<DataElement> dataElements = new ArrayList<>();
}

@Data
@Validated
class SelfReportingRequest
{
    @NotEmpty
    private String id;

    @NotEmpty
    private Integer diastolic;

    @NotEmpty
    private Integer systolic;

    @NotEmpty
    private Integer pulse;

    @NotEmpty
    private Double weight;
}

enum Status
{
    OK,
    ERROR
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