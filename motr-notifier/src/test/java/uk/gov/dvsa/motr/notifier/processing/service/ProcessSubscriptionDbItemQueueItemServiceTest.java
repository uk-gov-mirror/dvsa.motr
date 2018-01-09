package uk.gov.dvsa.motr.notifier.processing.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.notify.NotifyEmailService;
import uk.gov.dvsa.motr.notifier.notify.NotifySmsService;
import uk.gov.dvsa.motr.notifier.processing.model.ContactDetail;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;

import java.time.LocalDate;
import java.util.Optional;

import javax.ws.rs.core.UriBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProcessSubscriptionDbItemQueueItemServiceTest {

    private ProcessSubscriptionService processSubscriptionService;

    private VehicleDetailsClient vehicleDetailsClient = mock(VehicleDetailsClient.class);
    private SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private NotifyEmailService notifyEmailService = mock(NotifyEmailService.class);
    private NotifySmsService notifySmsService = mock(NotifySmsService.class);
    private String webBaseUrl = "http://gov.uk";

    private static final String TEST_VRM = "TEST-VRM";
    private static final String UPDATED_VRM = "UPDATED-VRM";
    private static final String SUBSCRIPTION_ID = "12345";
    private static final String MOT_TEST_NUMBER = "test-mot-number-123";
    private static final String TEST_MAKE = "TEST-MAKE";
    private static final String TEST_MODEL = "TEST-MODEL";
    private static final String TEST_MAKE_AND_MODEl = TEST_MAKE + " " + TEST_MODEL + ", ";
    private static final String DVLA_ID = "2131312";
    private static final String TEST_PHONE_NUMBER = "070000000000";

    @Before
    public void setUp() {
        processSubscriptionService = new ProcessSubscriptionService(
                vehicleDetailsClient, subscriptionRepository, notifyEmailService, notifySmsService, webBaseUrl
        );
    }

    @Test(expected = VehicleNotFoundException.class)
    public void whenVehicleNotFoundInTradeApiExceptionThrown() throws Exception {
        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.empty());
        processSubscriptionService.processSubscription(emailSubscriptionStub(LocalDate.now()), LocalDate.now());
    }

    @Test
    public void whenVehicleExpiryDateDiffersForSubscriptionItIsUpdatedInDatabase() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(LocalDate.of(2017, 10, 10)).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 8, 8);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(TEST_VRM, "test@this-is-a-test-123", vehicleExpiryDate);
    }

    @Test
    public void whenVehicleExpiryDateIsInOneMonthEmailReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 10);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(1)).sendOneMonthNotificationEmail(
                eq("test@this-is-a-test-123"),
                eq(TEST_MAKE_AND_MODEl + vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate),
                eq(getExpectedUnsubscribeLink()),
                any(String.class)
        );
    }

    @Test
    public void whenVehicleExpiryDateIsInOneMonthSmsReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = smsSubscriptionStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 10);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifySmsService, times(0)).sendTwoWeekNotificationSms(any(), any(), any());
        verify(notifySmsService, times(0)).sendOneDayAfterNotificationSms(any(), any());
        verify(notifySmsService, times(1)).sendOneMonthNotificationSms(
                eq(TEST_PHONE_NUMBER),
                eq(vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate)
        );
    }

    @Test
    public void whenVehicleExpiryDateIsInTwoWeeksEmailReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 10);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(1)).sendTwoWeekNotificationEmail(
                eq("test@this-is-a-test-123"),
                eq(TEST_MAKE_AND_MODEl + vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate),
                eq(getExpectedUnsubscribeLink()),
                any(String.class)
        );
    }

    @Test
    public void whenVehicleExpiryDateIsInTwoWeeksSmsReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = smsSubscriptionStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 10);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifySmsService, times(0)).sendOneMonthNotificationSms(any(), any(), any());
        verify(notifySmsService, times(0)).sendOneDayAfterNotificationSms(any(), any());
        verify(notifySmsService, times(1)).sendTwoWeekNotificationSms(
                eq(TEST_PHONE_NUMBER),
                eq(vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate)
        );
    }


    @Test
    public void whenVehicleExpiryDateOneDayAgoEmailReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 25);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(1)).sendOneDayAfterNotificationEmail(
                eq("test@this-is-a-test-123"),
                eq(TEST_MAKE_AND_MODEl + vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate),
                eq(getExpectedUnsubscribeLink()),
                any(String.class)
        );
    }

    @Test
    public void whenVehicleExpiryDateOneDayAgoSmsReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = smsSubscriptionStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 25);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscriptionQueueItem, requestDate);

        verify(subscriptionRepository, times(0)).updateExpiryDate(any(), any(), any());
        verify(notifySmsService, times(0)).sendOneMonthNotificationSms(any(), any(), any());
        verify(notifySmsService, times(0)).sendTwoWeekNotificationSms(any(), any(), any());
        verify(notifySmsService, times(1)).sendOneDayAfterNotificationSms(
                eq(TEST_PHONE_NUMBER),
                eq(vehicleDetails.getRegNumber())
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
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2014, 10, 24)).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 10);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(1)).sendTwoWeekNotificationEmail(
                eq("test@this-is-a-test-123"),
                eq(TEST_MAKE_AND_MODEl + vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate),
                eq(getExpectedUnsubscribeLink()),
                any(String.class)
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
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneDayAfterNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(1)).sendOneMonthNotificationEmail(
                eq("test@this-is-a-test-123"),
                eq(TEST_MAKE_AND_MODEl + vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate),
                eq(getExpectedUnsubscribeLink()),
                any(String.class)
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
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2014, 10, 24));
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 10, 25);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService, times(0)).sendTwoWeekNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(0)).sendOneMonthNotificationEmail(any(), any(), any(), any(), any());
        verify(notifyEmailService, times(1)).sendOneDayAfterNotificationEmail(
                eq("test@this-is-a-test-123"),
                eq(TEST_MAKE_AND_MODEl + vehicleDetails.getRegNumber()),
                eq(vehicleExpiryDate),
                eq(getExpectedUnsubscribeLink()),
                any(String.class)
        );
    }

    /**
     * When MOTR VRM is different from Trade API then the record is updated
     * @throws Exception
     */
    @Test
    public void whenStoredVrmIsDifferentFromTheVehicleApi_ThenRecordIsUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24)).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails.setRegNumber(UPDATED_VRM)));

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
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24)).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails.setRegNumber(TEST_VRM)));

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
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24)).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails.setMotTestNumber("123")));

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
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24)).setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(0)).updateMotTestNumber(TEST_VRM, "test@this-is-a-test-123", MOT_TEST_NUMBER);
    }

    /**
     * When we have an MOTR dvla id and we receive a mot test number from Trade API then the record is updated
     * @throws Exception
     */
    @Test
    public void whenWeHaveStoredDvlaId_andTradeApiReturnsTestNumber_thenRecordUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24)).setDvlaId(DVLA_ID);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 24);

        when(this.vehicleDetailsClient.fetchByDvlaId(any())).thenReturn(Optional.of(vehicleDetails.setMotTestNumber(MOT_TEST_NUMBER)));
        verify(this.vehicleDetailsClient, times(0)).fetchByMotTestNumber(MOT_TEST_NUMBER);

        processSubscriptionService.processSubscription(subscription, requestDate);

        verify(subscriptionRepository, times(1)).updateMotTestNumber(TEST_VRM, "test@this-is-a-test-123", MOT_TEST_NUMBER);
    }


    private SubscriptionQueueItem emailSubscriptionStub(LocalDate motDueDate) {

        return new SubscriptionQueueItem()
                .setId(SUBSCRIPTION_ID)
                .setMessageReceiptHandle("Test-receipt-handle")
                .setContactDetail(new ContactDetail("test@this-is-a-test-123", SubscriptionQueueItem.ContactType.EMAIL))
                .setMotDueDate(motDueDate)
                .setVrm(TEST_VRM);
    }

    private SubscriptionQueueItem smsSubscriptionStub(LocalDate motDueDate) {

        return new SubscriptionQueueItem()
                .setId(SUBSCRIPTION_ID)
                .setMessageReceiptHandle("Test-receipt-handle")
                .setContactDetail(new ContactDetail(TEST_PHONE_NUMBER, SubscriptionQueueItem.ContactType.MOBILE))
                .setMotDueDate(motDueDate)
                .setVrm(TEST_VRM);
    }

    private VehicleDetails vehicleDetailsStub(LocalDate expiryDate) {
        return new VehicleDetails().setMotExpiryDate(expiryDate).setRegNumber(TEST_VRM).setMake(TEST_MAKE).setModel(TEST_MODEL);
    }

    private String getExpectedUnsubscribeLink() {
        return UriBuilder.fromPath(this.webBaseUrl).path("unsubscribe").path(SUBSCRIPTION_ID).build().toString();
    }
}
