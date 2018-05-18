package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.service.notify.NotificationClient;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.EMAIL_CONFIRMATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.web.system.SystemVariable.GOV_NOTIFY_API_TOKEN;
import static uk.gov.dvsa.motr.web.system.SystemVariable.SMS_CONFIRM_PHONE_TEMPLATE_ID;
import static uk.gov.dvsa.motr.web.system.SystemVariable.SMS_SUBSCRIPTION_CONFIRMATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.web.system.SystemVariable.SUBSCRIPTION_CONFIRMATION_TEMPLATE_ID;

public class NotifyServiceFactory implements BaseFactory<NotifyService> {

    private final String apiKey;
    private final String emailSubscriptionConfirmationTemplateId;
    private final String emailConfirmationTemplateId;
    private final String smsConfirmationTemplateId;
    private final String smsSubscriptionConfirmationTemplateId;
    private final VehicleDetailsClient vehicleDetailsClient;
    private final UrlHelper urlHelper;

    @Inject
    public NotifyServiceFactory(
            @SystemVariableParam(GOV_NOTIFY_API_TOKEN) String apiKey,
            @SystemVariableParam(SUBSCRIPTION_CONFIRMATION_TEMPLATE_ID) String emailSubscriptionConfirmationTemplateId,
            @SystemVariableParam(EMAIL_CONFIRMATION_TEMPLATE_ID) String emailConfirmationTemplateId,
            @SystemVariableParam(SMS_CONFIRM_PHONE_TEMPLATE_ID) String smsConfirmationTemplateId,
            @SystemVariableParam(SMS_SUBSCRIPTION_CONFIRMATION_TEMPLATE_ID) String smsSubscriptionConfirmationTemplateId,
            VehicleDetailsClient vehicleDetailsClient,
            UrlHelper urlHelper) {

        this.apiKey = apiKey;
        this.emailSubscriptionConfirmationTemplateId = emailSubscriptionConfirmationTemplateId;
        this.emailConfirmationTemplateId = emailConfirmationTemplateId;
        this.smsConfirmationTemplateId = smsConfirmationTemplateId;
        this.smsSubscriptionConfirmationTemplateId = smsSubscriptionConfirmationTemplateId;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.urlHelper = urlHelper;
    }

    @Override
    public NotifyService provide() {

        NotificationClient client = new NotificationClient(apiKey);
        NotifyTemplateEngine templateEngine = new NotifyTemplateEngine();

        return new NotifyService(client,
                emailSubscriptionConfirmationTemplateId,
                emailConfirmationTemplateId,
                smsSubscriptionConfirmationTemplateId,
                smsConfirmationTemplateId,
                urlHelper,
                vehicleDetailsClient,
                templateEngine
        );
    }
}
