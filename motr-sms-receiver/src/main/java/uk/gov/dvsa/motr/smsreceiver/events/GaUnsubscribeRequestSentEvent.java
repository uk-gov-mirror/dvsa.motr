package uk.gov.dvsa.motr.smsreceiver.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class GaUnsubscribeRequestSentEvent extends Event {

    @Override
    public String getCode() {

        return "GA-HIT-SENT";
    }
}
