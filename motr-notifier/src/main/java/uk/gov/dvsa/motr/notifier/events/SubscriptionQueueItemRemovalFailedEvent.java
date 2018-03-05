package uk.gov.dvsa.motr.notifier.events;

public class SubscriptionQueueItemRemovalFailedEvent extends SubscriptionProcessedEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-QUEUE-ITEM-REMOVAL-FAILED";
    }
}
