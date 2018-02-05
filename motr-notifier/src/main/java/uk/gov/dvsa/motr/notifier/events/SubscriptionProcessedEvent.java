package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

import java.time.LocalDate;

public abstract class SubscriptionProcessedEvent extends Event {

    public SubscriptionProcessedEvent setMessageBody(String messageBody) {

        params.put("message-body", messageBody);
        return this;
    }

    public SubscriptionProcessedEvent setMessageProcessTimeProcessed(long processed) {

        params.put("time-to-process-message-ms", String.valueOf(processed));
        return this;
    }

    public SubscriptionProcessedEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public SubscriptionProcessedEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public SubscriptionProcessedEvent setExpiryDate(LocalDate expiryDate) {

        params.put("expiry-date", expiryDate.toString());
        return this;
    }

    public SubscriptionProcessedEvent setDvlaId(String dvlaId) {
        params.put("dvla-id", dvlaId);
        return this;
    }

    public SubscriptionProcessedEvent setMotTestNumber(String motTestNumber) {

        params.put("mot-test-number", motTestNumber);
        return this;
    }
}
