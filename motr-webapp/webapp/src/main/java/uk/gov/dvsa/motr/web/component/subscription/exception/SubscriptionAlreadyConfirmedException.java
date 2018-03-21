package uk.gov.dvsa.motr.web.component.subscription.exception;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

/**
 * Thrown when a existingSubscription against specific id.
 */
public class SubscriptionAlreadyConfirmedException extends Exception {

    private Subscription existingSubscription;

    public SubscriptionAlreadyConfirmedException(Subscription existingSubscription) {

        super(existingSubscription.getUnsubscribeId());

        this.existingSubscription = existingSubscription;
    }

    public Subscription getExistingSubscription() {

        return existingSubscription;
    }
}
