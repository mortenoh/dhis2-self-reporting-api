package org.hisp.dhis.integration.web;

import org.hisp.dhis.integration.domain.EmptyResponse;
import org.hisp.dhis.integration.domain.Status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */

@RestController
@RequestMapping( "/auth" )
@RequiredArgsConstructor
public class AuthController
{
    @PostMapping( "/register" )
    public ResponseEntity<EmptyResponse> register( @RequestBody Integer phoneNumber )
    {
        EmptyResponse response = EmptyResponse.builder()
            .status( Status.OK )
            .build();

        return ResponseEntity.ok( response );
    }

    @PostMapping( "/sendCode" )
    public ResponseEntity<EmptyResponse> sendCode( @RequestBody Integer phoneNumber )
    {
        EmptyResponse response = EmptyResponse.builder()
            .status( Status.OK )
            .build();

        return ResponseEntity.ok( response );
    }
}
