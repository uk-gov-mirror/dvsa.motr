package uk.gov.dvsa.motr.notifier.processing.model.notification.email;

import uk.gov.dvsa.motr.notifier.events.HgvPsvOneMonthEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.notify.DateFormatterForEmailDisplay;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

public class HgvPsvOneMonthEmailNotification extends SendableEmailNotification {

    public static final String MOT_EXPIRY_DATE_KEY = "mot_expiry_date";
    public static final String IS_DUE_OR_EXPIRES_KEY = "is_due_or_expires";

    public static final String HGV_PSV_PRESERVATION_STATEMENT_PREFIX =
            "You can get your annual test done from tomorrow to keep the same expiry date ";
    public static final String PRESERVATION_STATEMENT_KEY = "preservation_statement";

    public HgvPsvOneMonthEmailNotification(String webBaseUrl) {
        super(webBaseUrl);
        setNotificationPathSubject(HGV_PSV_DIRECTORY + "hgv-psv-one-month-notification-email-subject.txt");
        setNotificationPathBody(HGV_PSV_DIRECTORY + "hgv-psv-one-month-notification-email-body.txt");
    }

    @Override
    public void personalise(SubscriptionQueueItem subscription, VehicleDetails vehicleDetails) {
        super.personalise(subscription, vehicleDetails);

        personalisation.put(MOT_EXPIRY_DATE_KEY, DateFormatterForEmailDisplay.asFormattedForEmailDate(vehicleDetails.getMotExpiryDate()));
        personalisation.put(IS_DUE_OR_EXPIRES_KEY, vehicleHadItsFirstMotTest(vehicleDetails) ? "expires" : "is due");

        String preservationStatement = "";

        if (vehicleHadItsFirstMotTest(vehicleDetails)) {
            preservationStatement = HGV_PSV_PRESERVATION_STATEMENT_PREFIX +
                    DateFormatterForEmailDisplay.asFormattedForEmailDateWithoutYear(vehicleDetails.getMotExpiryDate()) +
                    PRESERVATION_STATEMENT_SUFFIX;
        }

        personalisation.put(PRESERVATION_STATEMENT_KEY, preservationStatement);
    }

    @Override
    public NotifyEvent getEvent() {
        return new HgvPsvOneMonthEmailReminderEvent();
    }
}
