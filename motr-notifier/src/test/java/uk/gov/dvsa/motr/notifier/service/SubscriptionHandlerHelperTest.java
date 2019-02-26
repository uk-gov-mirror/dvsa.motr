package uk.gov.dvsa.motr.notifier.service;

import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motDueDateUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motTestNumberUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneDayAfterNotificationRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneMonthNotificationRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.twoWeekNotificationRequired;

public class SubscriptionHandlerHelperTest {

    private static final String VEHICLE_DETAILS_NUMBER = "12012";
    private static final String SUBSCRIPTION_MOT_TEST_NUMBER = "21021";

    @Test
    public void subscriptionUpdateRequiredWhenDatesNotTheSame() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2018, 10, 10);

        assertTrue(motDueDateUpdateRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void subscriptionUpdateNotRequiredWhenDatesAreTheSame() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 10);

        assertFalse(motDueDateUpdateRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldBeSentIfDatesAre1MonthApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 11, 10);

        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldBeSentOnLastDayOfShortMonth() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 4, 30);

        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 5, 31);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldBeSentOnLastDayFebruaryForLastFourDaysInMarch() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 2, 28);

        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 3, 28);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        vehicleDetailsMotExpiryDate = LocalDate.of(2017, 3, 29);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        vehicleDetailsMotExpiryDate = LocalDate.of(2017, 3, 30);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        vehicleDetailsMotExpiryDate = LocalDate.of(2017, 3, 31);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldBeSentOnLastDayFebruaryForLastThreeDaysInMarchLeapYear() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2016, 2, 29);

        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 3, 28);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        vehicleDetailsMotExpiryDate = LocalDate.of(2016, 3, 29);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        vehicleDetailsMotExpiryDate = LocalDate.of(2016, 3, 30);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        vehicleDetailsMotExpiryDate = LocalDate.of(2016, 3, 31);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldNotBeSentIfDatesAreNot1MonthApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);

        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 11, 8);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        vehicleDetailsMotExpiryDate = LocalDate.of(2017, 11, 11);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldNotBeSentIfRequestDateIsNotAPreservationDate() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 1, 29);

        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 2, 28);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        subscriptionMotDueDate = LocalDate.of(2017, 1, 30);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        subscriptionMotDueDate = LocalDate.of(2017, 1, 31);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldNotBeNotSentOnJan30or31LeapYear() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2016, 1, 29);

        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2016, 2, 29);
        assertTrue(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        subscriptionMotDueDate = LocalDate.of(2016, 1, 31);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));

        subscriptionMotDueDate = LocalDate.of(2016, 1, 30);
        assertFalse(oneMonthNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void twoWeekSubscriptionShouldBeSentIfDatesAre14DaysApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 24);

        assertTrue(twoWeekNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void twoWeekSubscriptionShouldNotBeSentIfDatesAreNot14DaysApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 11);

        assertFalse(twoWeekNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneDayAfterSubscriptionShouldBeSentIfDateIsOneDayAfter() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 9);

        assertTrue(oneDayAfterNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneDayAfterSubscriptionShouldBeSentIfDateIsNotOneDayAfter() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 11);

        assertFalse(oneDayAfterNotificationRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void motTestNumberShouldBeUpdatedIfSubscriptionItemIsNullAndVehicleDetailsIsNot() {

        assertTrue(motTestNumberUpdateRequired(null, VEHICLE_DETAILS_NUMBER));
    }

    @Test
    public void motTestNumberShouldBeUpdatedIfSubscriptionItemIsDifferentVehicleDetailsTestNumber() {

        assertTrue(motTestNumberUpdateRequired(SUBSCRIPTION_MOT_TEST_NUMBER, VEHICLE_DETAILS_NUMBER));
    }

    @Test
    public void motTestNumberShouldNotBeUpdatedIfSubscriptionItemIsSameAsVehicleDetailsTestNumber() {

        assertFalse(motTestNumberUpdateRequired(VEHICLE_DETAILS_NUMBER, VEHICLE_DETAILS_NUMBER));
    }

    @Test
    public void motTestNumberShouldNotBeUpdatedIfSubscriptionItemIsNullAndSoIsVehicleDetails() {

        assertFalse(motTestNumberUpdateRequired(VEHICLE_DETAILS_NUMBER, VEHICLE_DETAILS_NUMBER));
    }
}
