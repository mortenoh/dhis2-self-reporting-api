package org.hisp.dhis.integration.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TrackedEntityAttributes {
    private List<Attribute> attributes = new ArrayList<>();
}
