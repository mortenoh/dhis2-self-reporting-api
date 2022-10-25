package org.hisp.dhis.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SelfReportingApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run( SelfReportingApplication.class, args );
    }
}
