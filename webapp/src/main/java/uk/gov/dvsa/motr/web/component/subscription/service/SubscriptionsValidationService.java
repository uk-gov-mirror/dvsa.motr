package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

import javax.inject.Inject;

public class SubscriptionsValidationService {

    private final SubscriptionRepository subscriptionRepository;

    @Inject
    public SubscriptionsValidationService(SubscriptionRepository subscriptionRepository) {

        this.subscriptionRepository = subscriptionRepository;
    }

    public boolean hasMaxTwoSubscriptionsForPhoneNumber(String number) {

        int count = subscriptionRepository.findByEmail(number);

        return count < 2;
    }
}
