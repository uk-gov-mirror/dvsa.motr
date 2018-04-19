package uk.gov.dvsa.motr.notifier.notify;

import com.amazonaws.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public NotifyEmailService(NotificationClient notificationClient, String oneMonthNotificationTemplateId, String
            twoWeekNotificationTemplateId, String oneDayAfterNotificationTemplateId) {

        this.notificationClient = notificationClient;
        this.oneMonthNotificationTemplateId = oneMonthNotificationTemplateId;
        this.twoWeekNotificationTemplateId = twoWeekNotificationTemplateId;
        this.oneDayAfterNotificationTemplateId = oneDayAfterNotificationTemplateId;
    }

    public SendEmailResponse sendOneMonthNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate,
            String unsubscribeLink, String motTestNumber, String mothUrl) throws NotificationClientException {

        Map<String, String> personalisation = genericPersonalisation(vehicleDetails, unsubscribeLink);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(motExpiryDate));
        personalisation.put("moth_url", mothUrl);

        if (StringUtils.isNullOrEmpty(motTestNumber)) {
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

        return sendEmail(
                emailAddress,
                this.oneMonthNotificationTemplateId,
                personalisation
        );
    }

    public SendEmailResponse sendTwoWeekNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate, String
            unsubscribeLink, String motTestNumber) throws NotificationClientException {

        Map<String, String> personalisation = genericPersonalisation(vehicleDetails, unsubscribeLink);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(motExpiryDate));

        if (StringUtils.isNullOrEmpty(motTestNumber)) {
            personalisation.put("is_due_or_expires", "is due");
        } else {
            personalisation.put("is_due_or_expires", "expires");
        }

        logger.debug("Personalisation for two week {}", personalisation);

        return sendEmail(
                emailAddress,
                this.twoWeekNotificationTemplateId,
                personalisation
        );
    }

    public SendEmailResponse sendOneDayAfterNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate,
            String unsubscribeLink, String motTestNumber) throws NotificationClientException {

        Map<String, String> personalisation = genericPersonalisation(vehicleDetails, unsubscribeLink);

        if (StringUtils.isNullOrEmpty(motTestNumber)) {

            personalisation.put("was_due_or_expired", "was due");
        } else {
            personalisation.put("was_due_or_expired", "expired");
        }

        logger.debug("Personalisation for one day after {}", personalisation);

        return sendEmail(
                emailAddress,
                this.oneDayAfterNotificationTemplateId,
                personalisation
        );
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
}
