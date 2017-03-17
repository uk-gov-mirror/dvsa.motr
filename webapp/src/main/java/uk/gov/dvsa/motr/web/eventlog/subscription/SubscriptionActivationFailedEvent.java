package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SubscriptionActivationFailedEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-ACTIVATION-FAILED";
    }
}
