package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.service.notify.NotificationClient;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.EMAIL_CONFIRMATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.web.system.SystemVariable.GOV_NOTIFY_API_TOKEN;
import static uk.gov.dvsa.motr.web.system.SystemVariable.SUBSCRIPTION_CONFIRMATION_TEMPLATE_ID;

public class NotifyServiceFactory implements BaseFactory<NotifyService> {

    private final Config config;

    @Inject
    public NotifyServiceFactory(Config config) {
        this.config = config;
    }

    @Override
    public NotifyService provide() {

        String apiKey = this.config.getValue(GOV_NOTIFY_API_TOKEN);
        String subscriptionConfirmationTemplateId = this.config.getValue(SUBSCRIPTION_CONFIRMATION_TEMPLATE_ID);
        String emailConfirmationTemplateId = this.config.getValue(EMAIL_CONFIRMATION_TEMPLATE_ID);
        NotificationClient client = new NotificationClient(apiKey);

        return new NotifyService(client, subscriptionConfirmationTemplateId, emailConfirmationTemplateId);
    }
}
