package uk.gov.dvsa.motr.web.eventlog;

import uk.gov.dvsa.motr.eventlog.Event;

public class PingEvent extends Event {

    @Override
    public String getCode() {
        return "PING";
    }


    public PingEvent setColdStart(boolean isColdStart) {

        params.put("cold-start", String.valueOf(isColdStart));
        return this;
    }
}
