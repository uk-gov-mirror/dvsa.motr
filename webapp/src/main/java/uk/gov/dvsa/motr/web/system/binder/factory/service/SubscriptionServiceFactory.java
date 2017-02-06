package uk.gov.dvsa.motr.web.system.binder.factory.service;

import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import javax.inject.Inject;

public class SubscriptionServiceFactory implements BaseFactory<SubscriptionService> {

    private final SubscriptionRepository subscriptionRepository;

    @Inject
    public SubscriptionServiceFactory(SubscriptionRepository subscriptionRepository) {

        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public SubscriptionService provide() {

        return new SubscriptionService(this.subscriptionRepository);
    }
}
