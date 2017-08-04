package uk.gov.dvsa.motr.smsreceiver.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class ReceivedMessageEvent extends Event {

    @Override
    public String getCode() {
        return "RECEIVED-MESSAGE";
    }

    public ReceivedMessageEvent setMessageBody(String messageBody) {

        params.put("message-body", messageBody);
        return this;
    }

    public ReceivedMessageEvent setRecipientMobile(String recipientMobile) {

        params.put("recipient-number", recipientMobile);
        return this;
    }
}
