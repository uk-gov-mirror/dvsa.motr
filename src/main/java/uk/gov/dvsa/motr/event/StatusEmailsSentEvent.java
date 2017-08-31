package uk.gov.dvsa.motr.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class StatusEmailsSentEvent extends Event {

    @Override
    public String getCode() {
        return "STATUS-EMAILS-SENT";
    }

    public StatusEmailsSentEvent setNumberOfSentEmails(int count) {

        params.put("sent-emails", count);

        return this;
    }
}
