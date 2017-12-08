package uk.gov.dvsa.motr.notifier.processing.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.notify.NotifyService;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;

import java.time.LocalDate;
import java.util.Optional;

import javax.ws.rs.core.UriBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProcessSubscriptionDbItemQueueItemServiceTest {

    private ProcessSubscriptionService processSubscriptionService;

    private VehicleDetailsClient vehicleDetailsClient = mock(VehicleDetailsClient.class);
    private SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private NotifyService notifyService = mock(NotifyService.class);
    private String webBaseUrl = "http://gov.uk";

    private static final String TEST_VRM = "TEST-VRM";
    private static final String UPDATED_VRM = "UPDATED-VRM";
    private static final String SUBSCRIPTION_ID = "12345";
    private static final String MOT_TEST_NUMBER = "test-mot-number-123";

    @Before
    public void setUp() {
        processSubscriptionService = new ProcessSubscriptionService(
                vehicleDetailsClient, subscriptionRepository, notifyService, webBaseUrl
        );
    }

    @Test(expected = VehicleNotFoundException.class)
    public void whenVehicleNotFoundInTradeApiExceptionThrown() throws Exception {
        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.empty());
        processSubscriptionService.processSubscription(subscriptionStub(LocalDate.now()), LocalDate.now());
    }

    @Test
    public void whenVehicleExpiryDateDiffersForSubscriptionItIsUpdatedInDatabase() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = subscriptionStub(LocalDate.of(2017, 10, 10));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 8, 8);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(TEST_VRM, "test@this-is-a-test-123", vehicleExpiryDate);
    }

    @Test
    public void whenVehicleExpiryDateIsInOneMonthReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = subscriptionStub(vehicleExpiryDate);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 10);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifyService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(1)).sendOneMonthNotificationEmail(
                "test@this-is-a-test-123", TEST_VRM, vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
    }

    @Test
    public void whenVehicleExpiryDateIsInTwoWeeksReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = subscriptionStub(vehicleExpiryDate);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 10);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifyService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(1)).sendTwoWeekNotificationEmail(
                "test@this-is-a-test-123", TEST_VRM, vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
    }

    @Test
    public void whenVehicleExpiryDateOneDayAgoReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = subscriptionStub(vehicleExpiryDate);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 25);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifyService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(1)).sendOneDayAfterNotificationEmail(
                "test@this-is-a-test-123", TEST_VRM, vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
    }

    /**
     * When MOTR expiry date is over a year in the past, then the record gets updated from MOTH record in the subscription database and if
     * it is within 2 weeks, an email is still sent
     * @throws Exception
     */
    @Test
    public void whenStoredExpiryDateIsBeforeTheVehicleApiButWithinTwoWeeksReminder_ThenRecordIsUpdatedAndEmailSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(LocalDate.of(2014, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 10);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(any(), any(), any());
        verify(notifyService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(1)).sendTwoWeekNotificationEmail(
                "test@this-is-a-test-123", TEST_VRM, vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
    }

    /**
     * When MOTR expiry date is over a year in the future, then the record gets updated from MOTH record in the subscription database and if
     * it is within one month, an email is still sent
     * @throws Exception
     */
    @Test
    public void whenStoredExpiryDateIsAfterTheVehicleApiButWithinOneMonthReminder_ThenRecordIsUpdatedAndEmailSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(LocalDate.of(2019, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(any(), any(), any());
        verify(notifyService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(1)).sendOneMonthNotificationEmail(
                "test@this-is-a-test-123", TEST_VRM, vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
    }

    /**
     * When MOTR expiry date is over a year in the past, then the record gets updated from MOTH record in the subscription database and if
     * it is 1 day after, an email is still sent
     * @throws Exception
     */
    @Test
    public void whenStoredExpiryDateIsBeforeTheVehicleApiButIsOneDayAfterReminder_ThenRecordIsUpdatedAndEmailSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(LocalDate.of(2014, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 25);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(any(), any(), any());
        verify(notifyService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any());
        verify(notifyService, times(1)).sendOneDayAfterNotificationEmail(
                "test@this-is-a-test-123", TEST_VRM, vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
    }

    /**
     * When MOTR VRM is different from Trade API then the record is updated
     * @throws Exception
     */
    @Test
    public void whenStoredVrmIsDifferentFromTheVehicleApi_ThenRecordIsUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(LocalDate.of(2019, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails.setRegNumber(UPDATED_VRM)));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateVrm(TEST_VRM, "test@this-is-a-test-123", UPDATED_VRM);
    }

    /**
     * When MOTR VRM is not different from Trade API then the record is not updated
     * @throws Exception
     */
    @Test
    public void whenStoredVrmIsTheSameFromTheVehicleApi_ThenRecordIsNotUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(LocalDate.of(2019, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails.setRegNumber(TEST_VRM)));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(0)).updateVrm(TEST_VRM, "test@this-is-a-test-123", TEST_VRM);
    }

    /**
     * When MOTR MOT test number is different from Trade API then the record is updated
     * @throws Exception
     */
    @Test
    public void whenStoredMotTestNumberIsDifferentFromTheVehicleApi_ThenRecordIsUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(LocalDate.of(2019, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails.setMotTestNumber("123")));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateMotTestNumber(TEST_VRM, "test@this-is-a-test-123", "123");
    }

    /**
     * When MOTR MOT test number is the same from Trade API then the record is not updated
     * @throws Exception
     */
    @Test
    public void whenStoredMotTestNumberIsTheSameFromTheVehicleApi_ThenRecordIsNotUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(LocalDate.of(2019, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetch(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(0)).updateMotTestNumber(TEST_VRM, "test@this-is-a-test-123", MOT_TEST_NUMBER);
    }

    private SubscriptionQueueItem subscriptionStub(LocalDate motDueDate) {
        return new SubscriptionQueueItem()
                .setId(SUBSCRIPTION_ID)
                .setMessageReceiptHandle("Test-receipt-handle")
                .setEmail("test@this-is-a-test-123")
                .setMotTestNumber(MOT_TEST_NUMBER)
                .setMotDueDate(motDueDate)
                .setVrm(TEST_VRM);
    }

    private VehicleDetails vehicleDetailsStub(LocalDate expiryDate) {
        return new VehicleDetails().setMotExpiryDate(expiryDate).setRegNumber(TEST_VRM);
    }

    private String getExpectedUnsubscribeLink() {
        return UriBuilder.fromPath(this.webBaseUrl).path("unsubscribe").path(SUBSCRIPTION_ID).build().toString();
    }
}
