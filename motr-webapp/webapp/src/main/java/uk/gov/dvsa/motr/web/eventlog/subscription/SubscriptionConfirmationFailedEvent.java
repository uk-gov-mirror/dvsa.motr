package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SubscriptionConfirmationFailedEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-CONFIRMATION-FAILED";
    }
}
