package uk.gov.dvsa.motr.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class CleanUpTriggeredEvent extends Event {

    @Override
    public String getCode() {
        return "CLEAN-UP-TRIGGERED";
    }
}
