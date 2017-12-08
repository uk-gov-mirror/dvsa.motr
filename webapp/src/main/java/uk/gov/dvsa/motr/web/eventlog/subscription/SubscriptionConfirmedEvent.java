package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SubscriptionConfirmedEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-CONFIRMED";
    }
}
