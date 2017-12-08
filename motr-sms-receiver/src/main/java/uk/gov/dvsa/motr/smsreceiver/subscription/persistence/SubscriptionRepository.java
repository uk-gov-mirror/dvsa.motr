package uk.gov.dvsa.motr.smsreceiver.subscription.persistence;

import uk.gov.dvsa.motr.smsreceiver.subscription.model.Subscription;

import java.util.Optional;

public interface SubscriptionRepository {

    Optional<Subscription> findByVrmAndMobileNumber(String vrm, String mobileNumber);

    void delete(Subscription subscription);
}
