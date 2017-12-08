package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DeleteSubscriptionSuccessfulEvent extends Event {

    @Override
    public String getCode() {

        return "SUCCESSFULLY-DELETED-SUBSCRIPTION";
    }

    public DeleteSubscriptionSuccessfulEvent setMotExpiryDate(LocalDate motExpiryDate) {

        params.put("mot-expiry-date", motExpiryDate.format(DateTimeFormatter.ISO_DATE));
        return this;
    }

    public DeleteSubscriptionSuccessfulEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }
}
