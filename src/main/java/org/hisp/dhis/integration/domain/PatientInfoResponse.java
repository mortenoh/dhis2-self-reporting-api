package org.hisp.dhis.integration.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientInfoResponse {
    private Status status;
    private PatientInfo info;
}
