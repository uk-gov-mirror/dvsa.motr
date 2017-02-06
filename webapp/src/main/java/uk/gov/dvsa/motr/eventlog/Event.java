package uk.gov.dvsa.motr.eventlog;

import java.util.HashMap;
import java.util.Map;

public abstract class Event {

    protected Map<String, String> params = new HashMap<>();

    Map<String, String> toMap() {
        return params;
    }

    public abstract String getCode();
}
