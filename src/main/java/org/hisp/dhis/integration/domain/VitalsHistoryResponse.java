package org.hisp.dhis.integration.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VitalsHistoryResponse {
    private Status status;
    private List<SelfReportingPayload> vitals;
}
