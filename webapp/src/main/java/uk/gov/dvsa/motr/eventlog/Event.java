package uk.gov.dvsa.motr.eventlog;

import java.util.Map;

public interface Event {

    Map<String, String> toMap();

    String getCode();
}
