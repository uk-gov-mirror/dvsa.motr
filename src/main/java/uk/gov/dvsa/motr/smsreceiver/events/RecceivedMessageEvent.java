package uk.gov.dvsa.motr.smsreceiver.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class RecceivedMessageEvent extends Event {

    @Override
    public String getCode() {
        return "RECEIVED-MESSAGE";
    }

    public RecceivedMessageEvent setMessageBody(String messageBody) {

        params.put("message-body", messageBody);
        return this;
    }
}
