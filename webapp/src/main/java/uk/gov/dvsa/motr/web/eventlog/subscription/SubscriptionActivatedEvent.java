package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SubscriptionActivatedEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-ACTIVATED";
    }
}
