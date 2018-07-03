package uk.gov.dvsa.motr.smsreceiver.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class FailedToSendGaUnsubscribeRequestEvent extends Event {

    @Override
    public String getCode() {

        return "FAILED-TO-SEND-GA-HIT";
    }
}
