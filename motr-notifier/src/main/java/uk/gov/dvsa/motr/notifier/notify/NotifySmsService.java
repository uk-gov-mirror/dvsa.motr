package uk.gov.dvsa.motr.notifier.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendSmsResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class NotifySmsService {

    private static final Logger logger = LoggerFactory.getLogger(NotifyEmailService.class);

    private NotificationClient notificationClient;
    private String oneMonthNotificationTemplateId;
    private String twoWeekNotificationTemplateId;
    private String oneDayAfterNotificationTemplateId;
    private String oneMonthNotificationTemplateIdPostEu;
    private String twoWeekNotificationTemplateIdPostEu;
    private String oneDayAfterNotificationTemplateIdPostEu;
    private String euGoLiveDate;

    public NotifySmsService(NotificationClient notificationClient, String oneMonthNotificationTemplateId, String
            twoWeekNotificationTemplateId, String oneDayAfterNotificationTemplateId, String oneMonthNotificationTemplateIdPostEu,
                            String twoWeekNotificationTemplateIdPostEu, String oneDayAfterNotificationTemplateIdPostEu,
                            String euGoLiveDate) {

        this.notificationClient = notificationClient;
        this.oneMonthNotificationTemplateId = oneMonthNotificationTemplateId;
        this.twoWeekNotificationTemplateId = twoWeekNotificationTemplateId;
        this.oneDayAfterNotificationTemplateId = oneDayAfterNotificationTemplateId;
        this.oneMonthNotificationTemplateIdPostEu = oneMonthNotificationTemplateIdPostEu;
        this.twoWeekNotificationTemplateIdPostEu = twoWeekNotificationTemplateIdPostEu;
        this.oneDayAfterNotificationTemplateIdPostEu = oneDayAfterNotificationTemplateIdPostEu;
        this.euGoLiveDate = euGoLiveDate;
    }

    public SendSmsResponse sendOneMonthNotificationSms(String phoneNumber, String vrm, LocalDate motExpiryDate)
            throws NotificationClientException {

        Map<String, String> personalisation = vrmAndExpiryDatePersonalisation(vrm, motExpiryDate);
        logger.debug("Sms Personalisation for one month {}", personalisation);

        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {
            return sendSms(phoneNumber, this.oneMonthNotificationTemplateIdPostEu, personalisation);
        }
        return sendSms(phoneNumber, this.oneMonthNotificationTemplateId, personalisation);
    }

    public SendSmsResponse sendTwoWeekNotificationSms(String phoneNumber, String vrm, LocalDate motExpiryDate)
            throws NotificationClientException {

        Map<String, String> personalisation = vrmAndExpiryDatePersonalisation(vrm, motExpiryDate);
        logger.debug("Sms Personalisation for two week {}", personalisation);

        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {
            return sendSms(phoneNumber, this.twoWeekNotificationTemplateIdPostEu, personalisation);
        }
        return sendSms(phoneNumber, this.twoWeekNotificationTemplateId, personalisation);
    }

    public SendSmsResponse sendOneDayAfterNotificationSms(String phoneNumber, String vrm)
            throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", vrm);
        logger.debug("Sms Personalisation for one day after {}", personalisation);

        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {
            return sendSms(phoneNumber, this.oneDayAfterNotificationTemplateIdPostEu, personalisation);
        }
        return sendSms(phoneNumber, this.oneDayAfterNotificationTemplateId, personalisation);
    }

    private SendSmsResponse sendSms(String phoneNumber, String smsTemplateId, Map<String, String> personalisation)
            throws NotificationClientException {

        return notificationClient.sendSms(smsTemplateId, phoneNumber, personalisation, "");
    }

    private Map<String, String> vrmAndExpiryDatePersonalisation(String vehicleDetails, LocalDate motExpiryDate) {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", vehicleDetails);
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(motExpiryDate));
        return personalisation;
    }

    public boolean isEuRoadworthinessLive(String euGoLiveDateFromConig) {

        LocalDate currentDate = LocalDate.now();
        LocalDate euGoLiveDate = LocalDate.parse(euGoLiveDateFromConig);

        return currentDate.isAfter(euGoLiveDate) || currentDate.isEqual(euGoLiveDate);
    }
}
