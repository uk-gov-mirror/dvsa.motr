package uk.gov.dvsa.motr.notifier.notify;

import com.amazonaws.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineFailedEvent;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NotifyEmailService {

    private static final Logger logger = LoggerFactory.getLogger(NotifyEmailService.class);

    private static final String PRESERVATION_STATEMENT_PREFIX =
            "You can get your MOT test done from tomorrow to keep the same MOT test date ";
    private static final String PRESERVATION_STATEMENT_SUFFIX = " for next year.";

    private NotificationClient notificationClient;
    private String oneMonthNotificationTemplateId;
    private String twoWeekNotificationTemplateId;
    private String oneDayAfterNotificationTemplateId;
    private String oneMonthNotificationTemplateIdPostEu;
    private String twoWeekNotificationTemplateIdPostEu;
    private String oneDayAfterNotificationTemplateIdPostEu;
    private String euGoLiveDate;
    private NotifyTemplateEngine notifyTemplateEngine;

    private static final String ONE_MONTH_NOTIFICATION_EMAIL_SUBJECT = "one-month-notification-email-subject.txt";
    private static final String ONE_MONTH_NOTIFICATION_EMAIL_BODY = "one-month-notification-email-body.txt";
    private static final String TWO_WEEK_NOTIFICATION_EMAIL_SUBJECT = "two-week-notification-email-subject.txt";
    private static final String TWO_WEEK_NOTIFICATION_EMAIL_BODY = "two-week-notification-email-body.txt";
    private static final String ONE_DAY_AFTER_NOTIFICATION_EMAIL_SUBJECT = "one-day-after-notification-email-subject.txt";
    private static final String ONE_DAY_AFTER_NOTIFICATION_EMAIL_BODY = "one-day-after-notification-email-body.txt";
    private static final String PRE_EU = "pre-eu/";

    public NotifyEmailService(NotificationClient notificationClient, String oneMonthNotificationTemplateId, String
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

    public SendEmailResponse sendOneMonthNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate,
            String unsubscribeLink, String dvlaId, String mothUrl) throws NotificationClientException {

        Map<String, String> personalisation = genericPersonalisation(vehicleDetails, unsubscribeLink);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(motExpiryDate));
        personalisation.put("moth_url", mothUrl);

        if (!StringUtils.isNullOrEmpty(dvlaId)) {
            personalisation.put("is_due_or_expires", "is due");
            personalisation.put("preservation_statement", "");
        } else {
            StringBuilder preservationStatementSb = new StringBuilder(128)
                    .append(PRESERVATION_STATEMENT_PREFIX)
                    .append(DateFormatterForEmailDisplay.asFormattedForEmailDateWithoutYear(motExpiryDate))
                    .append(PRESERVATION_STATEMENT_SUFFIX);

            personalisation.put("is_due_or_expires", "expires");
            personalisation.put("preservation_statement", preservationStatementSb.toString());
        }

        logger.debug("Personalisation for one month {}", personalisation);

        Map<String, String> notifyParams;

        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {
            notifyParams = getNotifyParameters(
                    ONE_MONTH_NOTIFICATION_EMAIL_SUBJECT,ONE_MONTH_NOTIFICATION_EMAIL_BODY,
                    personalisation);

            return sendEmail(
                    emailAddress,
                    this.oneMonthNotificationTemplateIdPostEu,
                    notifyParams
            );
        }

        notifyParams = getNotifyParameters(
                PRE_EU + ONE_MONTH_NOTIFICATION_EMAIL_SUBJECT, PRE_EU + ONE_MONTH_NOTIFICATION_EMAIL_BODY,
                personalisation);

        return sendEmail(
                emailAddress,
                this.oneMonthNotificationTemplateId,
                notifyParams
        );
    }

    public SendEmailResponse sendTwoWeekNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate, String
            unsubscribeLink, String dvlaId) throws NotificationClientException {

        Map<String, String> personalisation = genericPersonalisation(vehicleDetails, unsubscribeLink);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(motExpiryDate));

        if (!StringUtils.isNullOrEmpty(dvlaId)) {
            personalisation.put("is_due_or_expires", "is due");
        } else {
            personalisation.put("is_due_or_expires", "expires");
        }

        logger.debug("Personalisation for two week {}", personalisation);

        Map<String, String> notifyParams;
        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {

            notifyParams = getNotifyParameters(
                    TWO_WEEK_NOTIFICATION_EMAIL_SUBJECT,TWO_WEEK_NOTIFICATION_EMAIL_BODY,
                    personalisation);

            return sendEmail(
                    emailAddress,
                    this.twoWeekNotificationTemplateIdPostEu,
                    notifyParams
            );
        }

        notifyParams = getNotifyParameters(
                PRE_EU + TWO_WEEK_NOTIFICATION_EMAIL_SUBJECT, PRE_EU + TWO_WEEK_NOTIFICATION_EMAIL_BODY,
                personalisation);

        return sendEmail(
                emailAddress,
                this.twoWeekNotificationTemplateId,
                notifyParams
        );
    }

    public SendEmailResponse sendOneDayAfterNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate,
            String unsubscribeLink, String dvlaId) throws NotificationClientException {

        Map<String, String> personalisation = genericPersonalisation(vehicleDetails, unsubscribeLink);

        if (!StringUtils.isNullOrEmpty(dvlaId)) {

            personalisation.put("was_due_or_expired", "was due");
        } else {
            personalisation.put("was_due_or_expired", "expired");
        }

        logger.debug("Personalisation for one day after {}", personalisation);

        Map<String, String> notifyParams;
        if (this.isEuRoadworthinessLive(this.euGoLiveDate)) {

            notifyParams = getNotifyParameters(
                    ONE_DAY_AFTER_NOTIFICATION_EMAIL_SUBJECT, ONE_DAY_AFTER_NOTIFICATION_EMAIL_BODY,
                    personalisation);
            return sendEmail(
                    emailAddress,
                    this.oneDayAfterNotificationTemplateIdPostEu,
                    notifyParams
            );
        }

        notifyParams = getNotifyParameters(
                PRE_EU + ONE_DAY_AFTER_NOTIFICATION_EMAIL_SUBJECT, PRE_EU + ONE_DAY_AFTER_NOTIFICATION_EMAIL_BODY,
                personalisation);

        return sendEmail(
                emailAddress,
                this.oneDayAfterNotificationTemplateId,
                notifyParams
        );
    }

    private Map<String, String> getNotifyParameters(String subject, String body, Map<String, String> parameters)
            throws NotificationClientException {

        try {
            return notifyTemplateEngine.getNotifyParameters(subject, body, parameters);
        } catch (NotifyTemplateEngineException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new NotificationClientException(exception);
        }
    }

    private SendEmailResponse sendEmail(String emailAddress, String emailTemplateId, Map<String, String> personalisation)
            throws NotificationClientException {

        return notificationClient.sendEmail(emailTemplateId, emailAddress, personalisation, "");
    }

    private Map<String, String> genericPersonalisation(String vehicleDetails, String unsubscribeLink) {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_details", vehicleDetails);
        personalisation.put("unsubscribe_link", unsubscribeLink);

        return personalisation;
    }

    public boolean isEuRoadworthinessLive(String euGoLiveDateFromConfig) {

        LocalDate currentDate = LocalDate.now();
        LocalDate euGoLiveDate = LocalDate.parse(euGoLiveDateFromConfig);

        return currentDate.isAfter(euGoLiveDate) || currentDate.isEqual(euGoLiveDate);
    }
}
