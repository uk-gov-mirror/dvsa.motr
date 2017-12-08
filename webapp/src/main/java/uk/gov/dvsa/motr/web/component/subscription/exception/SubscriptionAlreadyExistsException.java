package uk.gov.dvsa.motr.web.component.subscription.exception;

/**
 * Thrown when a subscription against specific email and vrm already exists
 */
public class SubscriptionAlreadyExistsException extends Exception {

    public SubscriptionAlreadyExistsException(String message) {

        super(message);
    }
}
