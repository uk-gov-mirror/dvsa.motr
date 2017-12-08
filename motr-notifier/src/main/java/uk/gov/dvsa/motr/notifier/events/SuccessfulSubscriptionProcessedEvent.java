package uk.gov.dvsa.motr.notifier.events;

public class SuccessfulSubscriptionProcessedEvent extends SubscriptionProcessedEvent {

    @Override
    public String getCode() {

        return "SUCCESSFUL-PROCESSED-SUBSCRIPTION";
    }
}
