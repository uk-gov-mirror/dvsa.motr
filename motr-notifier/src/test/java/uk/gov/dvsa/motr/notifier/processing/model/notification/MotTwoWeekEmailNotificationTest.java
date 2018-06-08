package uk.gov.dvsa.motr.notifier.processing.model.notification;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotTwoWeekEmailNotification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotTwoWeekEmailNotification.IS_DUE_OR_EXPIRES_KEY;
import static uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotTwoWeekEmailNotification.MOT_EXPIRY_DATE_KEY;
import static uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification.UNSUBSCRIBE_LINK_KEY;
import static uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification.VEHICLE_DETAILS_KEY;

public class MotTwoWeekEmailNotificationTest {

    private static final String BASE_URL = "baseUrl.com";
    private MotTwoWeekEmailNotification notification;
    private SubscriptionQueueItem subscription = mock(SubscriptionQueueItem.class);
    private VehicleDetails vehicleDetails = mock(VehicleDetails.class);

    @Before
    public void setUp() {
        notification = new MotTwoWeekEmailNotification(BASE_URL);
        when(vehicleDetails.getMotExpiryDate()).thenReturn(LocalDate.parse("2018-05-21"));
        when(subscription.getId()).thenReturn("2");
    }

    @Test
    public void templateBodyPath_isCorrect() {
        assertEquals("two-week-notification-email-body.txt", notification.getNotificationPathBody());
    }

    @Test
    public void templateSubjectPath_isCorrect() {
        assertEquals("two-week-notification-email-subject.txt", notification.getNotificationPathSubject());
    }

    @Test
    public void containsVehicleDetails() {
        when(vehicleDetails.getRegNumber()).thenReturn("ABC123");
        when(vehicleDetails.getMake()).thenReturn("MAKE");
        when(vehicleDetails.getModel()).thenReturn("MODEL");

        notification.personalise(subscription, vehicleDetails);

        assertEquals("MAKE MODEL, ABC123", notification.getPersonalisation().get(VEHICLE_DETAILS_KEY));
    }

    @Test
    public void containsUnsubscribeLink() {
        when(subscription.getId()).thenReturn("1234");

        notification.personalise(subscription, vehicleDetails);

        assertEquals(String.format("%s/%s/%s", BASE_URL, "unsubscribe", "1234"),
                notification.getPersonalisation().get(UNSUBSCRIBE_LINK_KEY));
    }

    @Test
    public void containsExpiryDate() {
        notification.personalise(subscription, vehicleDetails);

        assertEquals("21 May 2018", notification.getPersonalisation().get(MOT_EXPIRY_DATE_KEY));
    }

    @Test
    public void whenVehicleHadNoMotTest_notificationSays_isDue() {
        when(vehicleDetails.getMotTestNumber()).thenReturn(null);

        notification.personalise(subscription, vehicleDetails);

        assertEquals("is due", notification.getPersonalisation().get(IS_DUE_OR_EXPIRES_KEY));
    }

    @Test
    public void whenVehicleHadAnMotTest_notificationSays_expires() {
        when(vehicleDetails.getMotTestNumber()).thenReturn("1");

        notification.personalise(subscription, vehicleDetails);

        assertEquals("expires", notification.getPersonalisation().get(IS_DUE_OR_EXPIRES_KEY));
    }
}
