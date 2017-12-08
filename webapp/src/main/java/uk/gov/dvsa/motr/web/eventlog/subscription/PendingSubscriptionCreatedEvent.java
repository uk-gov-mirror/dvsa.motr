package uk.gov.dvsa.motr.web.eventlog.subscription;

public class PendingSubscriptionCreatedEvent extends PendingSubscriptionEvent {

    @Override
    public String getCode() {

        return "PENDING-SUBSCRIPTION-CREATED";
    }
}
