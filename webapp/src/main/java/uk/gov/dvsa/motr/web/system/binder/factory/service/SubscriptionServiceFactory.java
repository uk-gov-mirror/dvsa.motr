package uk.gov.dvsa.motr.web.system.binder.factory.service;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;
import uk.gov.dvsa.motr.web.helper.UnsubscriptionUrlHelper;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import javax.inject.Inject;

public class SubscriptionServiceFactory implements BaseFactory<SubscriptionService> {

    private final SubscriptionRepository subscriptionRepository;
    private final NotifyService notifyService;
    private final UnsubscriptionUrlHelper unsubscriptionUrlHelper;

    @Inject
    public SubscriptionServiceFactory(
            NotifyService notifyService,
            SubscriptionRepository subscriptionRepository,
            UnsubscriptionUrlHelper unsubscriptionUrlHelper
    ) {

        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.unsubscriptionUrlHelper = unsubscriptionUrlHelper;
    }

    @Override
    public SubscriptionService provide() {

        return new SubscriptionService(this.subscriptionRepository, this.notifyService, this.unsubscriptionUrlHelper);
    }
}
