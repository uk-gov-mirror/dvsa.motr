package uk.gov.dvsa.motr.notifier.processing.service;

import uk.gov.dvsa.motr.notify.PreservationDateChecker;

import java.time.LocalDate;

/**
 * A helper to distinguish if a subscription needs updated in the DB or if an email needs to be sent to remind them
 */
public class SubscriptionHandlerHelper {

    private static final int MONTHS_BEFORE_DELETION = 59;
    private static final int TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS = 60;
    private static final int ONE_MONTH_AHEAD_NOTIFICATION_TIME_MONTHS = 1;
    private static final int TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS = 14;
    private static final int ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS = -1;

    public static boolean motDueDateUpdateRequired(LocalDate subscriptionMotDueDate, LocalDate vehicleDetailsMotExpiryDate) {

        System.out.println("Subscription Due Date: " + subscriptionMotDueDate.toString());
        System.out.println("Vehicle Details Expiry Date: " + vehicleDetailsMotExpiryDate.toString());
        return !subscriptionMotDueDate.equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean twoMonthNotificationRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusDays(TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean oneMonthNotificationRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        if (PreservationDateChecker.dateIs29February(requestDate)) {
            return isExpiryDateCorrectFor29February(requestDate, vehicleDetailsMotExpiryDate);
        }

        if (PreservationDateChecker.dateIs28FebruaryButNotLeapYear(requestDate)) {
            return isExpiryDateCorrectFor28February(requestDate, vehicleDetailsMotExpiryDate);
        }

        if (PreservationDateChecker.expiryMonthIsLongerThanPreviousMonthButNotMarch(requestDate)) {
            return isExpiryDateCorrectForMonthsLongerThanPreviousMonthsButNotMarch(requestDate, vehicleDetailsMotExpiryDate);
        }

        return PreservationDateChecker.isValidPreservationDate(requestDate)
                && requestDate.plusMonths(ONE_MONTH_AHEAD_NOTIFICATION_TIME_MONTHS).equals(vehicleDetailsMotExpiryDate);
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

    private static boolean isExpiryDateCorrectFor29February(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {
        return (requestDate.plusMonths(1).equals(vehicleDetailsMotExpiryDate)
                || requestDate.plusDays(31).equals(vehicleDetailsMotExpiryDate)
                || requestDate.plusDays(30).equals(vehicleDetailsMotExpiryDate)
            );
    }

    private static boolean isExpiryDateCorrectFor28February(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {
        return (requestDate.plusMonths(1).equals(vehicleDetailsMotExpiryDate)
                || requestDate.plusDays(31).equals(vehicleDetailsMotExpiryDate)
                || requestDate.plusDays(30).equals(vehicleDetailsMotExpiryDate)
                || requestDate.plusDays(29).equals(vehicleDetailsMotExpiryDate)
            );
    }

    private static boolean isExpiryDateCorrectForMonthsLongerThanPreviousMonthsButNotMarch(
            LocalDate requestDate,
            LocalDate vehicleDetailsMotExpiryDate) {
        return (requestDate.plusMonths(1).equals(vehicleDetailsMotExpiryDate)
                || requestDate.plusDays(31).equals(vehicleDetailsMotExpiryDate)
            );
    }
}
