package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class SubscriptionEvent extends Event {

    public SubscriptionEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public SubscriptionEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public SubscriptionEvent setExpiryDate(LocalDate expiryDate) {

        params.put("mot_expiry_date", expiryDate.format(DateTimeFormatter.ISO_DATE));
        return this;
    }
}
