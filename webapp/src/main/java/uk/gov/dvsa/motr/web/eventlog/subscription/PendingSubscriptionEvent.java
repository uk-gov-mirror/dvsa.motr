package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class PendingSubscriptionEvent extends Event {

    public PendingSubscriptionEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public PendingSubscriptionEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public PendingSubscriptionEvent setExpiryDate(LocalDate expiryDate) {

        params.put("mot-expiry-date", expiryDate.format(DateTimeFormatter.ISO_DATE));
        return this;
    }
}
