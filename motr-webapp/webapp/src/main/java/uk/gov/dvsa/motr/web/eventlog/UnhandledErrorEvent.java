package uk.gov.dvsa.motr.web.eventlog;

import uk.gov.dvsa.motr.eventlog.Event;

public class UnhandledErrorEvent extends Event {

    @Override
    public String getCode() {
        return "UNHANDLED_ERROR";
    }
}
