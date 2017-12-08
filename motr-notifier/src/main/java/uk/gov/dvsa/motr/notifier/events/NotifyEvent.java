package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;

import java.time.LocalDate;

public abstract class NotifyEvent extends Event {

    public NotifyEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public NotifyEvent setContactType(SubscriptionQueueItem.ContactType contactType) {

        params.put("contact-type", contactType.getValue());
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

    public NotifyEvent setDvlaId(String dvlaId) {

        params.put("dvla-id", dvlaId);
        return this;
    }

    public NotifyEvent setExpiryDate(LocalDate expiryDate) {

        params.put("expiry-date", expiryDate.toString());
        return this;
    }
}
