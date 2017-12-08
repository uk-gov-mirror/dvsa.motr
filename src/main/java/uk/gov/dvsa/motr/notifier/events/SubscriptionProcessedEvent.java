package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

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
}
