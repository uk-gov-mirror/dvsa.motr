package uk.gov.dvsa.motr.notifier.events;

public class SubscriptionProcessingFailedEvent extends SubscriptionProcessedEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-PROCESSING-FAILED";
    }
}
