package uk.gov.dvsa.motr.notifications.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineFailedEvent;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent;
import uk.gov.dvsa.motr.web.formatting.DateFormatter;
import uk.gov.dvsa.motr.web.formatting.DateFormatterForSmsDisplay;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent.Type.EMAIL_CONFIRMATION;
import static uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent.Type.PHONE_NUMBER_CONFIRMATION;
import static uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent.Type.SMS_SUBSCRIPTION_CONFIRMATION;
import static uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent.Type.SUBSCRIPTION_CONFIRMATION;

public class NotifyService {

    private final NotificationClient notificationClient;
    private final String emailSubscriptionConfirmationTemplateId;
    private final String emailConfirmationTemplateId;
    private final String smsSubscriptionConfirmationTemplateId;
    private final String smsConfirmationTemplateId;
    private final UrlHelper urlHelper;
    private final VehicleDetailsClient vehicleDetailsClient;
    private final NotifyTemplateEngine notifyTemplateEngine;

    private static final String HGV_PSV_TEMPLATES_DIRECTORY = "hgv-psv/";

    private static final String SIGN_UP_CONFIRM_EMAIL_SUBJECT = "sign-up-confirm-email-subject.txt";
    private static final String SIGN_UP_CONFIRM_EMAIL_BODY = "sign-up-confirm-email-body.txt";
    private static final String SIGN_UP_CONFIRM_SMS = "sign-up-confirm-sms.txt";

    private static final String SIGNED_UP_COMPLETE_EMAIL_SUBJECT = "signed-up-complete-email-subject.txt";
    private static final String SIGNED_UP_COMPLETE_EMAIL_BODY = "signed-up-complete-email-body.txt";
    private static final String SIGNED_UP_COMPLETE_SMS = "signed-up-complete-sms.txt";

    public NotifyService(
            NotificationClient notificationClient,
            String emailSubscriptionConfirmationTemplateId,
            String emailConfirmationTemplateId,
            String smsSubscriptionConfirmationTemplateId,
            String smsConfirmationTemplateId,UrlHelper urlHelper,
            VehicleDetailsClient vehicleDetailsClient,
            NotifyTemplateEngine notifyTemplateEngine
    ) {
        this.notificationClient = notificationClient;
        this.emailSubscriptionConfirmationTemplateId = emailSubscriptionConfirmationTemplateId;
        this.emailConfirmationTemplateId = emailConfirmationTemplateId;
        this.smsSubscriptionConfirmationTemplateId = smsSubscriptionConfirmationTemplateId;
        this.smsConfirmationTemplateId = smsConfirmationTemplateId;
        this.urlHelper = urlHelper;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.notifyTemplateEngine = notifyTemplateEngine;
    }

    public void sendSubscriptionConfirmation(Subscription subscription) {
        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(subscription.getVrm(), vehicleDetailsClient);

        if (subscription.getContactDetail().getContactType() == Subscription.ContactType.EMAIL) {
            sendSubscriptionCompleteEmail(subscription, vehicleDetails);
        } else {
            sendSubscriptionConfirmationSms(subscription, vehicleDetails);
        }
    }

    public void sendEmailAddressConfirmationEmail(String emailAddress, String confirmationLink, String vehicleDetails) {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("confirmation_link", confirmationLink);
        personalisation.put("vehicle_details", vehicleDetails);

        Map<String, String> notifyParams = getNotifyParameters(
                SIGN_UP_CONFIRM_EMAIL_SUBJECT, SIGN_UP_CONFIRM_EMAIL_BODY,
                personalisation);

        try {
            notificationClient.sendEmail(emailConfirmationTemplateId, emailAddress, notifyParams, "");

        } catch (NotificationClientException e) {
            EventLogger.logErrorEvent(new NotifyClientFailedEvent().setContact(emailAddress).setType(EMAIL_CONFIRMATION), e);
            // wrapping because nothing can be done about it
            throw new RuntimeException(e);
        }
    }

    public void sendPhoneNumberConfirmationSms(String phoneNumber, String confirmationCode) {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("confirmation_code", confirmationCode);

        Map<String, String> notifyParams = getNotifyParameters(SIGN_UP_CONFIRM_SMS, personalisation);

        try {

            notificationClient.sendSms(smsConfirmationTemplateId, phoneNumber, notifyParams, "");

        } catch (NotificationClientException e) {
            EventLogger.logErrorEvent(new NotifyClientFailedEvent().setContact(phoneNumber).setType(PHONE_NUMBER_CONFIRMATION), e);
            // wrapping because nothing can be done about it
            throw new RuntimeException(e);
        }
    }

    private void sendSubscriptionCompleteEmail(
            Subscription subscription,
            VehicleDetails vehicleDetails
    ) {
        String emailAddress = subscription.getContactDetail().getValue();
        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_details", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(
                vehicleDetails, ", " + subscription.getVrm()));
        personalisation.put("mot_expiry_date", DateFormatter.asDisplayDate(subscription.getMotDueDate()));
        personalisation.put("unsubscribe_link", urlHelper.unsubscribeLink(subscription.getUnsubscribeId()));

        if (subscription.getMotIdentification().getDvlaId().isPresent()) {
            personalisation.put("is_due_or_expires", "is due");
        } else {
            personalisation.put("is_due_or_expires", "expires");
        }

        Map<String, String> notifyParams = getNotifyParameters(
                modifyTemplatePathForVehicleType(SIGNED_UP_COMPLETE_EMAIL_SUBJECT, vehicleDetails.getVehicleType()),
                modifyTemplatePathForVehicleType(SIGNED_UP_COMPLETE_EMAIL_BODY, vehicleDetails.getVehicleType()),
                personalisation);

        try {

            notificationClient.sendEmail(emailSubscriptionConfirmationTemplateId, emailAddress, notifyParams, "");

        } catch (NotificationClientException e) {

            EventLogger.logErrorEvent(new NotifyClientFailedEvent().setContact(emailAddress).setType(SUBSCRIPTION_CONFIRMATION), e);
            // wrapping because nothing can be done about it
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getNotifyParameters(String subject, String body, Map<String, String> parameters) {
        try {
            return notifyTemplateEngine.getNotifyParameters(subject, body, parameters);
        } catch (NotifyTemplateEngineException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new RuntimeException(exception);
        }
    }

    private Map<String, String> getNotifyParameters(String body, Map<String, String> parameters) {
        try {
            return notifyTemplateEngine.getNotifyParameters(body, parameters);
        } catch (NotifyTemplateEngineException exception) {

            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new RuntimeException(exception);
        }
    }

    private void sendSubscriptionConfirmationSms(Subscription subscription, VehicleDetails vehicleDetails) {

        String phoneNumber = subscription.getContactDetail().getValue();
        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", subscription.getVrm());
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(subscription.getMotDueDate()));
        Map<String, String> notifyParams = getNotifyParameters(
                modifyTemplatePathForVehicleType(SIGNED_UP_COMPLETE_SMS, vehicleDetails.getVehicleType()), personalisation);

        try {

            notificationClient.sendSms(smsSubscriptionConfirmationTemplateId, phoneNumber, notifyParams, "");

        } catch (NotificationClientException e) {

            EventLogger.logErrorEvent(new NotifyClientFailedEvent().setContact(phoneNumber).setType(SMS_SUBSCRIPTION_CONFIRMATION), e);
            // wrapping because nothing can be done about it
            throw new RuntimeException(e);
        }
    }

    private String modifyTemplatePathForVehicleType(String templatePath, VehicleType vehicleType) {
        if (VehicleType.isCommercialVehicle(vehicleType)) {
            templatePath = HGV_PSV_TEMPLATES_DIRECTORY + templatePath;
        }
        return templatePath;
    }

}
