package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

import java.time.LocalDate;

public abstract class NotifyEvent extends Event {

    public NotifyEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public NotifyEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public NotifyEvent setMotTestNumber(String motTestNumber) {
        
        params.put("mot-test-number", motTestNumber);
        return this;
    }

    public NotifyEvent setExpiryDate(LocalDate expiryDate) {

        params.put("expiry-date", expiryDate.toString());
        return this;
    }
}
