package uk.gov.dvsa.motr.web.component.subscription.exception;

public class PendingSubscriptionAlreadyExistsException extends Exception {

    public PendingSubscriptionAlreadyExistsException(String message) {

        super(message);
    }
}
