package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;
import uk.gov.dvsa.motr.web.system.binder.factory.service.SubscriptionServiceFactory;

public class ServiceBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bindFactory(SubscriptionServiceFactory.class).to(SubscriptionService.class);
    }
}
