package uk.gov.dvsa.motr.web.eventlog;

import uk.gov.dvsa.motr.eventlog.Event;

public class HoneyPotTriggeredEvent extends Event {

    @Override
    public String getCode() {

        return "HONEY_POT";
    }
}