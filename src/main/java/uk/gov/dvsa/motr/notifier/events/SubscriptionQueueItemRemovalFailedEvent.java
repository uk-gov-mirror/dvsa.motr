package uk.gov.dvsa.motr.notifier.events;

import java.time.LocalDate;

public class SubscriptionQueueItemRemovalFailedEvent extends SubscriptionProcessedEvent {

    public SubscriptionQueueItemRemovalFailedEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public SubscriptionQueueItemRemovalFailedEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public SubscriptionQueueItemRemovalFailedEvent setMotTestNumber(String motTestNumber) {
        params.put("mot-test-number", motTestNumber);
        return this;
    }

    public SubscriptionQueueItemRemovalFailedEvent setExpiryDate(LocalDate expiryDate) {

        params.put("expiry-date", expiryDate.toString());
        return this;
    }

    @Override
    public String getCode() {

        return "SUBSCRIPTION-QUEUE-ITEM-REMOVAL-FAILED";
    }
}
