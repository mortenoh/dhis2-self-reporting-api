package org.hisp.dhis.integration.util;

public interface ValueSetter<HOLDER> {
    void setValue(HOLDER holder, String value);
}
