package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

public class RepositoryBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(DynamoDbSubscriptionRepository.class).to(SubscriptionRepository.class);
    }
}
