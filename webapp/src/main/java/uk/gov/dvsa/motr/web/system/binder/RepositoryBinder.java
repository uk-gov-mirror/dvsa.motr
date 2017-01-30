package uk.gov.dvsa.motr.web.system.binder;


import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.system.binder.factory.repository.DynamoDbSubscriptionRepositoryFactory;

public class RepositoryBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bindFactory(DynamoDbSubscriptionRepositoryFactory.class).to(SubscriptionRepository.class);
    }
}
