package org.hisp.dhis.integration.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "self-reporting")
public class SelfReportingProperties {
    @NotEmpty
    private String baseUrl;

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String programId;

    @NotEmpty
    private String firstNameAttribute;

    @NotEmpty
    private String lastNameAttribute;

    @NotEmpty
    private String dobAttribute;
}
