package uk.gov.dvsa.motr.notifier.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineFailedEvent;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendSmsResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
    private NotifyTemplateEngine notifyTemplateEngine;

    private static final String ONE_MONTH_NOTIFICATION_SMS = "one-month-notification-sms.txt";
    private static final String TWO_WEEK_NOTIFICATION_SMS = "two-week-notification-sms.txt";
    private static final String ONE_DAY_AFTER_NOTIFICATION_SMS = "one-day-after-notification-sms.txt";
    private static final String PRE_EU = "pre-eu/";

    public NotifySmsService(NotificationClient notificationClient, String oneMonthNotificationTemplateId, String
            twoWeekNotificationTemplateId, String oneDayAfterNotificationTemplateId, String oneMonthNotificationTemplateIdPostEu,
                            String twoWeekNotificationTemplateIdPostEu, String oneDayAfterNotificationTemplateIdPostEu,
                            String euGoLiveDate, NotifyTemplateEngine notifyTemplateEngine) {

        this.notificationClient = notificationClient;
        this.oneMonthNotificationTemplateId = oneMonthNotificationTemplateId;
        this.twoWeekNotificationTemplateId = twoWeekNotificationTemplateId;
        this.oneDayAfterNotificationTemplateId = oneDayAfterNotificationTemplateId;
        this.oneMonthNotificationTemplateIdPostEu = oneMonthNotificationTemplateIdPostEu;
        this.twoWeekNotificationTemplateIdPostEu = twoWeekNotificationTemplateIdPostEu;
        this.oneDayAfterNotificationTemplateIdPostEu = oneDayAfterNotificationTemplateIdPostEu;
        this.euGoLiveDate = euGoLiveDate;
        this.notifyTemplateEngine = notifyTemplateEngine;
    }

    public SendSmsResponse sendOneMonthNotificationSms(String phoneNumber, String vrm, LocalDate motExpiryDate)
            throws NotificationClientException {

        Map<String, String> personalisation = vrmAndExpiryDatePersonalisation(vrm, motExpiryDate);
        logger.debug("Sms Personalisation for one month {}", personalisation);
        Map<String, String> notifyParams;
        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {
            notifyParams = getNotifyParameters(ONE_MONTH_NOTIFICATION_SMS, personalisation);
            return sendSms(phoneNumber, this.oneMonthNotificationTemplateIdPostEu, notifyParams);
        }
        notifyParams = getNotifyParameters(PRE_EU + ONE_MONTH_NOTIFICATION_SMS, personalisation);
        return sendSms(phoneNumber, this.oneMonthNotificationTemplateId, notifyParams);
    }

    public SendSmsResponse sendTwoWeekNotificationSms(String phoneNumber, String vrm, LocalDate motExpiryDate)
            throws NotificationClientException {

        Map<String, String> personalisation = vrmAndExpiryDatePersonalisation(vrm, motExpiryDate);
        logger.debug("Sms Personalisation for two week {}", personalisation);
        Map<String, String> notifyParams;
        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {
            notifyParams = getNotifyParameters(TWO_WEEK_NOTIFICATION_SMS, personalisation);
            return sendSms(phoneNumber, this.twoWeekNotificationTemplateIdPostEu, notifyParams);
        }

        notifyParams = getNotifyParameters(PRE_EU + TWO_WEEK_NOTIFICATION_SMS, personalisation);
        return sendSms(phoneNumber, this.twoWeekNotificationTemplateId, notifyParams);
    }

    public SendSmsResponse sendOneDayAfterNotificationSms(String phoneNumber, String vrm)
            throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", vrm);
        logger.debug("Sms Personalisation for one day after {}", personalisation);

        Map<String, String> notifyParams;
        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {
            notifyParams = getNotifyParameters(ONE_DAY_AFTER_NOTIFICATION_SMS, personalisation);
            return sendSms(phoneNumber, this.oneDayAfterNotificationTemplateIdPostEu, notifyParams);
        }
        notifyParams = getNotifyParameters(PRE_EU + ONE_DAY_AFTER_NOTIFICATION_SMS, personalisation);
        return sendSms(phoneNumber, this.oneDayAfterNotificationTemplateId, notifyParams);
    }

    private SendSmsResponse sendSms(String phoneNumber, String smsTemplateId, Map<String, String> personalisation)
            throws NotificationClientException {

        return notificationClient.sendSms(smsTemplateId, phoneNumber, personalisation, "");
    }

    private Map<String, String> getNotifyParameters(String body, Map<String, String> parameters) throws NotificationClientException {
        try {
            return notifyTemplateEngine.getNotifyParameters(body, parameters);
        } catch (NotifyTemplateEngineException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new NotificationClientException(exception);
        }
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
