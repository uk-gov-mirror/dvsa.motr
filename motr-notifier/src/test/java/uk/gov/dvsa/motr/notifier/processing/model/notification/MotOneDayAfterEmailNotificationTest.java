package uk.gov.dvsa.motr.notifier.processing.model.notification;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotOneDayAfterEmailNotification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotOneDayAfterEmailNotification.WAS_DUE_OR_EXPIRED_KEY;
import static uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification.UNSUBSCRIBE_LINK_KEY;
import static uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification.VEHICLE_DETAILS_KEY;

public class MotOneDayAfterEmailNotificationTest {

    private static final String BASE_URL = "baseUrl.com";
    private MotOneDayAfterEmailNotification notification;
    private SubscriptionQueueItem subscription = mock(SubscriptionQueueItem.class);
    private VehicleDetails vehicleDetails = mock(VehicleDetails.class);

    @Before
    public void setUp() {
        notification = new MotOneDayAfterEmailNotification(BASE_URL);
        when(vehicleDetails.getMotExpiryDate()).thenReturn(LocalDate.parse("2018-05-21"));
        when(subscription.getId()).thenReturn("2");
    }

    @Test
    public void templateBodyPath_isCorrect() {
        assertEquals("one-day-after-notification-email-body.txt", notification.getNotificationPathBody());
    }

    @Test
    public void templateSubjectPath_isCorrect() {
        assertEquals("one-day-after-notification-email-subject.txt", notification.getNotificationPathSubject());
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
    public void whenVehicleHadNoMotTest_notificationSays_isDue() {
        when(vehicleDetails.getMotTestNumber()).thenReturn(null);

        notification.personalise(subscription, vehicleDetails);

        assertEquals("was due", notification.getPersonalisation().get(WAS_DUE_OR_EXPIRED_KEY));
    }

    @Test
    public void whenVehicleHadAnMotTest_notificationSays_expires() {
        when(vehicleDetails.getMotTestNumber()).thenReturn("1");

        notification.personalise(subscription, vehicleDetails);

        assertEquals("expired", notification.getPersonalisation().get(WAS_DUE_OR_EXPIRED_KEY));
    }


}
