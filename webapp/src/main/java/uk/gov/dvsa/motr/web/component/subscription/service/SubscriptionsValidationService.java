package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.formatting.PhoneNumberFormatter;

import javax.inject.Inject;

public class SubscriptionsValidationService {

    private final SubscriptionRepository subscriptionRepository;

    @Inject
    public SubscriptionsValidationService(SubscriptionRepository subscriptionRepository) {

        this.subscriptionRepository = subscriptionRepository;
    }

    public boolean hasMaxTwoSubscriptionsForPhoneNumber(String number) {

        String normalizedUkPhoneNumber = PhoneNumberFormatter.normalizeUkPhoneNumber(number);

        int count = subscriptionRepository.findByEmail(normalizedUkPhoneNumber);

        return count < 2;
    }
}
