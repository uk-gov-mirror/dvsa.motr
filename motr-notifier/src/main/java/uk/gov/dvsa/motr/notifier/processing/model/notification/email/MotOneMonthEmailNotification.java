package uk.gov.dvsa.motr.notifier.processing.model.notification.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.OneMonthEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.notify.DateFormatterForEmailDisplay;
import uk.gov.dvsa.motr.notifier.notify.MotHistoryUrlFormatter;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.security.NoSuchAlgorithmException;

public class MotOneMonthEmailNotification extends SendableEmailNotification {

    private static final Logger logger = LoggerFactory.getLogger(MotHistoryUrlFormatter.class);
    public static final String MOT_EXPIRY_DATE_KEY = "mot_expiry_date";
    public static final String IS_DUE_OR_EXPIRES_KEY = "is_due_or_expires";
    public static final String PRESERVATION_STATEMENT_KEY = "preservation_statement";
    public static final String MOTH_URL_KEY = "moth_url";

    private String mothDirectUrlPrefix;
    private String checksumSalt;

    public MotOneMonthEmailNotification(String webBaseUrl, String mothDirectUrlPrefix, String checksumSalt) {
        super(webBaseUrl);
        this.mothDirectUrlPrefix = mothDirectUrlPrefix;
        this.checksumSalt = checksumSalt;
        setNotificationPathSubject("one-month-notification-email-subject.txt");
        setNotificationPathBody("one-month-notification-email-body.txt");
    }

    @Override
    public void personalise(SubscriptionQueueItem subscription, VehicleDetails vehicleDetails) {
        super.personalise(subscription, vehicleDetails);

        personalisation.put(MOT_EXPIRY_DATE_KEY, DateFormatterForEmailDisplay.asFormattedForEmailDate(vehicleDetails.getMotExpiryDate()));
        try {
            personalisation.put(MOTH_URL_KEY, MotHistoryUrlFormatter.getUrl(mothDirectUrlPrefix, vehicleDetails, checksumSalt));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to generate URL checksum:", e);
        }

        if (vehicleHadItsFirstMotTest(vehicleDetails)) {
            String preservationStatement = MOT_PRESERVATION_STATEMENT_PREFIX +
                    DateFormatterForEmailDisplay.asFormattedForEmailDateWithoutYear(vehicleDetails.getMotExpiryDate()) +
                    PRESERVATION_STATEMENT_SUFFIX;

            personalisation.put(IS_DUE_OR_EXPIRES_KEY, "expires");
            personalisation.put(PRESERVATION_STATEMENT_KEY, preservationStatement);

        } else {
            personalisation.put(IS_DUE_OR_EXPIRES_KEY, "is due");
            personalisation.put(PRESERVATION_STATEMENT_KEY, "");
        }
    }

    @Override
    public NotifyEvent getEvent() {
        return new OneMonthEmailReminderEvent();
    }
}
