package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.helper.EmailConfirmationUrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.helper.UnsubscriptionUrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.service.PendingSubscriptionService;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;
import uk.gov.dvsa.motr.web.system.binder.factory.NotifyServiceFactory;

public class ServiceBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bindFactory(NotifyServiceFactory.class).to(NotifyService.class);
        bind(PendingSubscriptionService.class).to(PendingSubscriptionService.class);
        bind(SubscriptionConfirmationService.class).to(SubscriptionConfirmationService.class);
        bind(UnsubscribeService.class).to(UnsubscribeService.class);
        bind(UnsubscriptionUrlHelper.class).to(UnsubscriptionUrlHelper.class);
        bind(EmailConfirmationUrlHelper.class).to(EmailConfirmationUrlHelper.class);
    }
}
