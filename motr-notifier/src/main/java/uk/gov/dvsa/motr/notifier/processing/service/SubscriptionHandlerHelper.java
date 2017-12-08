package uk.gov.dvsa.motr.notifier.processing.service;

import java.time.LocalDate;

/**
 * A helper to distinguish if a subscription needs updated in the DB
 * or if an email needs to be sent to remind them
 */
public class SubscriptionHandlerHelper {

    private static final int MONTHS_BEFORE_DELETION = 59;

    public static boolean motDueDateUpdateRequired(LocalDate subscriptionMotDueDate, LocalDate vehicleDetailsMotExpiryDate) {

        return !subscriptionMotDueDate.equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean oneMonthEmailRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusMonths(1).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean twoWeekEmailRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusDays(14).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean oneDayAfterEmailRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.minusDays(1).equals(vehicleDetailsMotExpiryDate);
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
        return  (requestDate.isAfter(deletionDate) || requestDate.isEqual(deletionDate));
    }
}
