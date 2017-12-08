package uk.gov.dvsa.motr.notifier.processing.service;

import java.time.LocalDate;

/**
 * A helper to distinguish if a subscription needs updated in the DB
 * or if an email needs to be sent to remind them
 */
public class SubscriptionHandlerHelper {

    public static boolean motDueDateUpdateRequired(LocalDate subscriptionMotDueDate, LocalDate vehicleDetailsMotExpiryDate) {

        return !subscriptionMotDueDate.equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean oneMonthEmailRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusMonths(1).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean twoWeekEmailRequired(LocalDate requestDate, LocalDate vehicleDetailsMotExpiryDate) {

        return requestDate.plusDays(14).equals(vehicleDetailsMotExpiryDate);
    }

    public static boolean motTestNumberUpdateRequired(String subscriptionMotTestNumber, String vehicleDetailsMotTestNumber) {
        return !subscriptionMotTestNumber.equals(vehicleDetailsMotTestNumber);
    }

    public static boolean vrmUpdateRequired(String subscriptionVrm, String vehicleDetailsVrm) {
        return !subscriptionVrm.equals(vehicleDetailsVrm);
    }
}
