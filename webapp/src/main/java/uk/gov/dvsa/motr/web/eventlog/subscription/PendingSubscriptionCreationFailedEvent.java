package uk.gov.dvsa.motr.web.eventlog.subscription;

public class PendingSubscriptionCreationFailedEvent extends PendingSubscriptionEvent {

    @Override
    public String getCode() {

        return "PENDING-SUBSCRIPTION-CREATION-FAILED";
    }
}
