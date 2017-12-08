package uk.gov.dvsa.motr.smsreceiver.events;


import uk.gov.dvsa.motr.eventlog.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SuccessfullyUnsubscribedEvent extends Event {

    @Override
    public String getCode() {

        return "UNSUBSCRIBED";
    }

    public SuccessfullyUnsubscribedEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public SuccessfullyUnsubscribedEvent setDueDate(LocalDate motDueDate) {

        params.put("mot-due-date", motDueDate.format(DateTimeFormatter.ISO_DATE));
        return this;
    }

    public SuccessfullyUnsubscribedEvent setReasonForCancellation(String reasonForCancellation) {
        params.put("reason_for_cancellation", reasonForCancellation);
        return this;
    }
}
