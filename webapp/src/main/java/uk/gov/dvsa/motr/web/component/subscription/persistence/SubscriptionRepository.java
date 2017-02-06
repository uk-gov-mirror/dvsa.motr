package uk.gov.dvsa.motr.web.component.subscription.persistence;

import org.jvnet.hk2.annotations.Contract;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.util.Optional;


public interface SubscriptionRepository {

    Optional<Subscription> findById(String id);

    Optional<Subscription> findByVrmAndEmail(String vrm, String email);

    void save(Subscription subscription);
}
