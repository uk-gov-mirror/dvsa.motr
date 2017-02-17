package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SubscriptionConfirmationSuccessfulEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-CONFIRMATION-SUCCESS";
    }
}
