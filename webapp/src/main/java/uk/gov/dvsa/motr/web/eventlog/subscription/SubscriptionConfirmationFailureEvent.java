package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SubscriptionConfirmationFailureEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-CONFIRMATION-FAILURE";
    }
}
