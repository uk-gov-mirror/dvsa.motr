package uk.gov.dvsa.motr.notifier.processing.service;

import java.time.LocalDate;

/**
 * A helper to distinguish if a subscription needs updated in the DB or if an email needs to be sent to remind them
 */
public class SubscriptionHandlerHelper {

    private static final int MONTHS_BEFORE_DELETION = 59;
    private static final int TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS = 60;
    private static final int ONE_MONTH_AHEAD_NOTIFICATION_TIME_DAYS = 30;
    private static final int TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS = 14;
    private static final int ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS = -1;

    public static boolean motDueDateUpdateRequired(LocalDate subscriptionMotDueDate, LocalDate vehicleDetailsMotExpiryDate) {

        return !subscriptionMotDueDate.equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean twoMonthNotificationRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusDays(TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean oneMonthNotificationRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        //return requestDate.plusDays(ONE_MONTH_AHEAD_NOTIFICATION_TIME_DAYS).equals(vehicleDetailsMotExpiryDate);
        return true;
    }

    public static boolean twoWeekNotificationRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusDays(TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean oneDayAfterNotificationRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusDays(ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean motTestNumberUpdateRequired(String subscriptionMotTestNumber, String vehicleDetailsMotTestNumber) {

        return (subscriptionMotTestNumber == null && vehicleDetailsMotTestNumber != null)
                || (subscriptionMotTestNumber != null && vehicleDetailsMotTestNumber != null
                && !subscriptionMotTestNumber.equals(vehicleDetailsMotTestNumber));
    }

    public static boolean vrmUpdateRequired(String subscriptionVrm, String vehicleDetailsVrm) {

        return !subscriptionVrm.equals(vehicleDetailsVrm);
    }

    public static boolean subscriptionDeletionRequired(LocalDate vehicleMotExpiryDate, LocalDate requestDate) {

        LocalDate deletionDate = vehicleMotExpiryDate.plusMonths(MONTHS_BEFORE_DELETION);
        return (requestDate.isAfter(deletionDate) || requestDate.isEqual(deletionDate));
    }
}
