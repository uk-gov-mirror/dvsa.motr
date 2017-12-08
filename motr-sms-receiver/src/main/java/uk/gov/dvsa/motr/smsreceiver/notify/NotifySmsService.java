package uk.gov.dvsa.motr.smsreceiver.notify;


import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendSmsResponse;

import java.util.HashMap;
import java.util.Map;

public class NotifySmsService {

    private NotificationClient notificationClient;
    private String smsUnsubscriptionConfirmationTemplateId;

    public NotifySmsService(NotificationClient notificationClient, String smsUnsubscriptionConfirmationTemplateId) {

        this.notificationClient = notificationClient;
        this.smsUnsubscriptionConfirmationTemplateId = smsUnsubscriptionConfirmationTemplateId;
    }

    public SendSmsResponse sendUnsubscriptionConfirmationSms(String phoneNumber, String vrm)
            throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", vrm);
        return sendSms(this.smsUnsubscriptionConfirmationTemplateId, phoneNumber, personalisation);
    }

    private SendSmsResponse sendSms(String smsTemplateId, String phoneNumber, Map<String, String> personalisation)
            throws NotificationClientException {

        return notificationClient.sendSms(smsTemplateId, phoneNumber, personalisation, "");
    }
}
