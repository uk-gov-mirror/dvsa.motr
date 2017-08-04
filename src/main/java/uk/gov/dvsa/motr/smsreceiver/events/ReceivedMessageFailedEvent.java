package uk.gov.dvsa.motr.smsreceiver.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class ReceivedMessageFailedEvent extends Event {

    @Override
    public String getCode() {

        return "RECEIVED-MESSAGE-FAILED";
    }
}
