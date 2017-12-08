package uk.gov.dvsa.motr.smsreceiver.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class UnableToProcessMessageEvent extends Event {

    @Override
    public String getCode() {

        return "UNABLE-TO-PROCESS-MESSAGE";
    }

    public UnableToProcessMessageEvent setReason(String reason) {

        params.put("reason", reason);
        return this;
    }
}
