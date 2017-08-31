package uk.gov.dvsa.motr.eventlog;

import java.util.HashMap;
import java.util.Map;

public abstract class Event {

    protected Map<String, Object> params = new HashMap<>();

    Map<String, Object> toMap() {
        return params;
    }

    public abstract String getCode();
}
