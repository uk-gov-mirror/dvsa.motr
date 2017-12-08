package uk.gov.dvsa.motr.web.eventlog.session;

import uk.gov.dvsa.motr.eventlog.Event;

public class SessionMalformedEvent extends Event {

    @Override
    public String getCode() {

        return "SESSION-MALFORMED-ERROR";
    }
}
