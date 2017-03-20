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

        verify(subscriptionRepository, times(1)).updateExpiryDate("TEST-VRM", "test@this-is-a-test-123", vehicleExpiryDate);
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
        verify(notifyService, times(1)).sendOneMonthNotificationEmail(
                "test@this-is-a-test-123", "TEST-VRM", vehicleExpiryDate, getExpectedUnsubscribeLink()
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
        verify(notifyService, times(1)).sendTwoWeekNotificationEmail(
                "test@this-is-a-test-123", "TEST-VRM", vehicleExpiryDate, getExpectedUnsubscribeLink()
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
        verify(notifyService, times(1)).sendTwoWeekNotificationEmail(
                "test@this-is-a-test-123", "TEST-VRM", vehicleExpiryDate, getExpectedUnsubscribeLink()
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
        verify(notifyService, times(1)).sendOneMonthNotificationEmail(
                "test@this-is-a-test-123", "TEST-VRM", vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
        verify(notifyService, times(0)).sendTwoWeekNotificationEmail(
                "test@this-is-a-test-123", "TEST-VRM", vehicleExpiryDate, getExpectedUnsubscribeLink()
        );
    }

    private SubscriptionQueueItem subscriptionStub(LocalDate motDueDate) {
        return new SubscriptionQueueItem()
                .setId("12345")
                .setMessageReceiptHandle("Test-receipt-handle")
                .setEmail("test@this-is-a-test-123")
                .setMotDueDate(motDueDate)
                .setVrm("TEST-VRM");
    }

    private VehicleDetails vehicleDetailsStub(LocalDate expiryDate) {
        return new VehicleDetails().setMotExpiryDate(expiryDate);
    }

    private String getExpectedUnsubscribeLink() {
        return UriBuilder.fromPath(this.webBaseUrl).path("unsubscribe").path("12345").build().toString();
    }
}
