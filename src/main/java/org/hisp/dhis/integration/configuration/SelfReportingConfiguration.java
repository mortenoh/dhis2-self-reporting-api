package org.hisp.dhis.integration.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class SelfReportingConfiguration
{
    private final SelfReportingProperties properties;

    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplateBuilder().defaultMessageConverters()
            .defaultHeader( "Content-Type", MediaType.APPLICATION_JSON_VALUE )
            .defaultHeader( "Accept", MediaType.APPLICATION_JSON_VALUE )
            .basicAuthentication( properties.getUsername(),
                properties.getPassword(), StandardCharsets.UTF_8 )
            .build();
    }
}
