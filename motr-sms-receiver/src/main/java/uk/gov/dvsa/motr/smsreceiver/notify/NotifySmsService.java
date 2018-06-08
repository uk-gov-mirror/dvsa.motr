package uk.gov.dvsa.motr.smsreceiver.notify;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineFailedEvent;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendSmsResponse;

import java.util.HashMap;
import java.util.Map;

public class NotifySmsService {

    private NotificationClient notificationClient;
    private String smsUnsubscriptionConfirmationTemplateId;
    private NotifyTemplateEngine notifyTemplateEngine;

    private static final String UNSUBSCRIPTION_CONFIRM_SMS = "unsubscription-confirm-sms.txt";
    private static final String HGV_PSV_UNSUBSCRIPTION_CONFIRM_SMS = "hgv-psv-unsubscription-confirm-sms.txt";

    public NotifySmsService(
            NotificationClient notificationClient,
            String smsUnsubscriptionConfirmationTemplateId,
            NotifyTemplateEngine notifyTemplateEngine
    ) {

        this.notificationClient = notificationClient;
        this.smsUnsubscriptionConfirmationTemplateId = smsUnsubscriptionConfirmationTemplateId;
        this.notifyTemplateEngine = notifyTemplateEngine;
    }

    public SendSmsResponse sendUnsubscriptionConfirmationSms(String phoneNumber, String vrm, VehicleType vehicleType)
            throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", vrm);

        Map<String, String> notifyParams;
        try {
            notifyParams = notifyTemplateEngine.getNotifyParameters(getTemplateForVehicle(vehicleType), personalisation);
        } catch (NotifyTemplateEngineException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new NotificationClientException(exception);
        }
        return sendSms(this.smsUnsubscriptionConfirmationTemplateId, phoneNumber, notifyParams);
    }

    private String getTemplateForVehicle(VehicleType vehicleType) {
        if (vehicleType == VehicleType.HGV || vehicleType == VehicleType.PSV) {
            return HGV_PSV_UNSUBSCRIPTION_CONFIRM_SMS;
        }
        return UNSUBSCRIPTION_CONFIRM_SMS;
    }

    private SendSmsResponse sendSms(String smsTemplateId, String phoneNumber, Map<String, String> personalisation)
            throws NotificationClientException {

        return notificationClient.sendSms(smsTemplateId, phoneNumber, personalisation, "");
    }
}
