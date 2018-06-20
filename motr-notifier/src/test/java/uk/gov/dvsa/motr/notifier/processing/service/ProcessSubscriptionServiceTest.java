package uk.gov.dvsa.motr.notifier.processing.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.notify.NotifyEmailService;
import uk.gov.dvsa.motr.notifier.notify.NotifySmsService;
import uk.gov.dvsa.motr.notifier.processing.factory.SendableNotificationFactory;
import uk.gov.dvsa.motr.notifier.processing.model.ContactDetail;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotTwoWeekEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotOneDayAfterSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotOneMonthSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotTwoWeekSmsNotification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProcessSubscriptionServiceTest {

    private ProcessSubscriptionService processSubscriptionService;

    private VehicleDetailsClient vehicleDetailsClient = mock(VehicleDetailsClient.class);
    private SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private NotifyEmailService notifyEmailService = mock(NotifyEmailService.class);
    private NotifySmsService notifySmsService = mock(NotifySmsService.class);
    private SendableNotificationFactory notificationFactory = mock(SendableNotificationFactory.class);

    private static final String WEB_BASE_URL = "http://gov.uk";
    private static final String TEST_VRM = "TEST-VRM";
    private static final String UPDATED_VRM = "UPDATED-VRM";
    private static final String SUBSCRIPTION_ID = "12345";
    private static final String MOT_TEST_NUMBER = "test-mot-number-123";
    private static final String NEW_MOT_TEST_NUMBER = "test-mot-number-234";
    private static final String TEST_MAKE = "TEST-MAKE";
    private static final String TEST_MODEL = "TEST-MODEL";
    private static final String DVLA_ID = "2131312";
    private static final String TEST_PHONE_NUMBER = "070000000000";

    @Before
    public void setUp() {
        processSubscriptionService = new ProcessSubscriptionService(
                vehicleDetailsClient, subscriptionRepository, notifyEmailService, notifySmsService, notificationFactory
        );
    }

    @Test(expected = VehicleNotFoundException.class)
    public void whenVehicleNotFoundInTradeApiExceptionThrown() throws Exception {
        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.empty());
        processSubscriptionService.processSubscription(emailSubscriptionStub(LocalDate.now()));
    }

    @Test
    public void whenVehicleExpiryDateDiffersForSubscriptionItIsUpdatedInDatabase() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(LocalDate.of(2017, 10, 10))
                .setLoadedOnDate(LocalDate.of(2017, 8, 8))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository).updateExpiryDate(TEST_VRM, "test@this-is-a-test-123", vehicleExpiryDate);
    }

    @Test
    public void whenVehicleExpiryDateIsInOneMonthEmailReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(vehicleExpiryDate)
                .setLoadedOnDate(LocalDate.of(2017, 9, 10))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService).sendEmail(
                eq("test@this-is-a-test-123"), isA(SendableEmailNotification.class), eq(vehicleDetails)
        );
    }

    @Test
    public void whenVehicleExpiryDateIsInOneMonthSmsReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 10);
        SubscriptionQueueItem subscriptionQueueItem = smsSubscriptionStub(vehicleExpiryDate)
                .setLoadedOnDate(LocalDate.of(2017, 9, 10))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);
        LocalDate requestDate = LocalDate.of(2017, 9, 10);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getSmsNotification(any(), any())).thenReturn(Optional.of(new MotOneMonthSmsNotification()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notifySmsService).sendSms(
                eq(TEST_PHONE_NUMBER), isA(MotOneMonthSmsNotification.class)
        );
    }

    @Test
    public void whenVehicleExpiryDateIsInTwoWeeksEmailReminderIsSent() throws Exception {
        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        LocalDate requestDate = LocalDate.of(2017, 10, 10);
        SubscriptionQueueItem subscription = emailSubscriptionStub(vehicleExpiryDate)
                .setLoadedOnDate(requestDate)
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notificationFactory).getEmailNotification(eq(requestDate), eq(subscription),eq(vehicleDetails));
        verify(notifyEmailService).sendEmail(any(), isA(SendableEmailNotification.class), any());
    }

    @Test
    public void whenVehicleExpiryDateIsInTwoWeeksSmsReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = smsSubscriptionStub(vehicleExpiryDate)
                .setLoadedOnDate(LocalDate.of(2017, 10, 10))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getSmsNotification(any(), any())).thenReturn(Optional.of(new MotTwoWeekSmsNotification()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notifySmsService).sendSms(
                eq(TEST_PHONE_NUMBER), isA(MotTwoWeekSmsNotification.class)
        );
    }

    @Test
    public void whenVehicleExpiryDateOneDayAgoEmailReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(vehicleExpiryDate)
                .setLoadedOnDate(LocalDate.of(2017, 10, 25))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService).sendEmail(
                eq("test@this-is-a-test-123"), isA(SendableEmailNotification.class), eq(vehicleDetails)
        );
    }

    @Test
    public void whenVehicleExpiryDateOneDayAgoSmsReminderIsSent() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscriptionQueueItem = smsSubscriptionStub(vehicleExpiryDate)
                .setLoadedOnDate(LocalDate.of(2017, 10, 25))
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getSmsNotification(any(), any())).thenReturn(Optional.of(new MotOneDayAfterSmsNotification()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notifySmsService).sendSms(
                eq(TEST_PHONE_NUMBER), isA(MotOneDayAfterSmsNotification.class)
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
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2014, 10, 24))
                .setLoadedOnDate(LocalDate.of(2017, 9, 24))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService).sendEmail(
                eq("test@this-is-a-test-123"), isA(SendableEmailNotification.class), eq(vehicleDetails)
        );
    }

    /**
     * When MOTR expiry date saved in subscription is over a year in the future,
     * then the vehicle data is still verified with MOTH record
     * and if the expiry date differs,
     * then MOTR record is updated,
     * and if the expiry date is within one month,
     * then an email notification is still sent
     * @throws Exception
     */
    @Test
    public void whenExpiryDateFromApiIsBeforeOneStoredInSubscriptionButWithinOneMonth_ThenRecordIsUpdatedAndEmailSent()
            throws Exception {
        LocalDate testExpiryDateInSubscription = LocalDate.of(2019, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(testExpiryDateInSubscription)
                .setLoadedOnDate(LocalDate.of(2017, 9, 24))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        LocalDate testExpiryDateInMotHistory = LocalDate.of(2017, 10, 24);
        VehicleDetails vehicleDetails = vehicleDetailsStub(testExpiryDateInMotHistory)
                .setMotTestNumber(NEW_MOT_TEST_NUMBER);

        when(vehicleDetailsClient.fetchByMotTestNumber(MOT_TEST_NUMBER)).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService).sendEmail(
                eq("test@this-is-a-test-123"), isA(SendableEmailNotification.class), eq(vehicleDetails)
        );
    }

    /**
     * When MOTR expiry date saved in subscription is over a year in the past,
     * then the subscription gets updated from MOTH record
     * and if the expiry date is 1 day after, an email is still sent
     * @throws Exception
     */
    @Test
    public void whenExpiryDateFromApiIsLaterThanStoredInSubscriptionButIsWithinOneDay_ThenRecordIsUpdatedAndEmailSent()
            throws Exception {

        LocalDate testExpiryDateInSubscription = LocalDate.of(2014, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(testExpiryDateInSubscription)
                .setVehicleType(VehicleType.MOT)
                .setLoadedOnDate(LocalDate.of(2017, 10, 25))
                .setMotTestNumber(MOT_TEST_NUMBER);
        LocalDate testExpiryDateInMotHistory = LocalDate.of(2017, 10, 24);
        VehicleDetails vehicleDetails = vehicleDetailsStub(testExpiryDateInMotHistory)
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(NEW_MOT_TEST_NUMBER);
        when(vehicleDetailsClient.fetchByMotTestNumber(MOT_TEST_NUMBER))
                .thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any()))
                .thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService).sendEmail(
                eq("test@this-is-a-test-123"), isA(SendableEmailNotification.class), eq(vehicleDetails)
        );
    }

    /**
     * When MOTR VRM is different from Trade API then the record is updated
     * @throws Exception
     */
    @Test
    public void whenStoredVrmIsDifferentFromTheVehicleApi_ThenRecordIsUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24))
                .setLoadedOnDate(LocalDate.of(2017, 9, 24))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails.setRegNumber(UPDATED_VRM)));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository).updateVrm(TEST_VRM, "test@this-is-a-test-123", UPDATED_VRM);
    }

    /**
     * When MOTR VRM is not different from Trade API then the record is not updated
     * @throws Exception
     */
    @Test
    public void whenStoredVrmIsTheSameFromTheVehicleApi_ThenRecordIsNotUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24))
                .setLoadedOnDate(LocalDate.of(2017, 9, 24))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails.setRegNumber(TEST_VRM)));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository, never()).updateVrm(TEST_VRM, "test@this-is-a-test-123", TEST_VRM);
    }

    /**
     * When MOTR MOT test number is different from Trade API then the record is updated
     * @throws Exception
     */
    @Test
    public void whenStoredMotTestNumberIsDifferentFromTheVehicleApi_ThenRecordIsUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24))
                .setLoadedOnDate(LocalDate.of(2017, 9, 24))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails.setMotTestNumber("123")));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository).updateMotTestNumber(TEST_VRM, "test@this-is-a-test-123", "123");
    }

    /**
     * When MOTR MOT test number is the same from Trade API then the record is not updated
     * @throws Exception
     */
    @Test
    public void whenStoredMotTestNumberIsTheSameFromTheVehicleApi_ThenRecordIsNotUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24))
                .setLoadedOnDate(LocalDate.of(2017, 9, 24))
                .setVehicleType(VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate).setMotTestNumber(MOT_TEST_NUMBER);

        when(vehicleDetailsClient.fetchByMotTestNumber(any())).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository, never()).updateMotTestNumber(TEST_VRM, "test@this-is-a-test-123", MOT_TEST_NUMBER);
    }

    /**
     * When we have an MOTR dvla id and we receive a mot test number from Trade API then the record is updated
     * @throws Exception
     */
    @Test
    public void whenWeHaveStoredDvlaId_andTradeApiReturnsTestNumber_thenRecordUpdated() throws Exception {

        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 24);
        SubscriptionQueueItem subscription = emailSubscriptionStub(LocalDate.of(2019, 10, 24))
                .setLoadedOnDate(LocalDate.of(2017, 9, 24))
                .setVehicleType(VehicleType.MOT)
                .setDvlaId(DVLA_ID);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        when(vehicleDetailsClient.fetchByDvlaId(any())).thenReturn(Optional.of(vehicleDetails.setMotTestNumber(MOT_TEST_NUMBER)));
        when(notificationFactory.getEmailNotification(any(), any(), any())).thenReturn(Optional.of(sendableEmailNotificationStub()));
        verify(vehicleDetailsClient, never()).fetchByMotTestNumber(MOT_TEST_NUMBER);

        processSubscriptionService.processSubscription(subscription);

        verify(subscriptionRepository).updateMotTestNumber(TEST_VRM, "test@this-is-a-test-123", MOT_TEST_NUMBER);
    }

    @Test
    public void whenHgvVehicleExpiryDateIsInOneMonthEmailReminderIsSent() throws Exception {

        LocalDate testExpiryDate = LocalDate.of(2017, 10, 10);
        LocalDate requestDate = LocalDate.of(2017, 9, 10);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(testExpiryDate)
                .setLoadedOnDate(requestDate)
                .setVehicleType(VehicleType.HGV)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(testExpiryDate);

        when(vehicleDetailsClient.fetchHgvPsvByVrm(TEST_VRM)).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(requestDate, subscriptionQueueItem, vehicleDetails))
                .thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService).sendEmail(
                eq("test@this-is-a-test-123"), isA(SendableEmailNotification.class), eq(vehicleDetails)
        );
    }

    @Test
    public void whenPsvVehicleExpiryDateIsInTwoMonthsEmailReminderIsSent() throws Exception {

        LocalDate testExpiryDate = LocalDate.of(2017, 10, 10);
        LocalDate requestDate = LocalDate.of(2017, 8, 12);
        SubscriptionQueueItem subscriptionQueueItem = emailSubscriptionStub(testExpiryDate)
                .setLoadedOnDate(requestDate)
                .setVehicleType(VehicleType.HGV)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(testExpiryDate);

        when(vehicleDetailsClient.fetchHgvPsvByVrm(TEST_VRM)).thenReturn(Optional.of(vehicleDetails));
        when(notificationFactory.getEmailNotification(requestDate, subscriptionQueueItem, vehicleDetails))
                .thenReturn(Optional.of(sendableEmailNotificationStub()));

        processSubscriptionService.processSubscription(subscriptionQueueItem);

        verify(subscriptionRepository, never()).updateExpiryDate(any(), any(), any());
        verify(notifyEmailService).sendEmail(
                eq("test@this-is-a-test-123"), isA(SendableEmailNotification.class), eq(vehicleDetails)
        );
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

    private SendableEmailNotification sendableEmailNotificationStub() {
        return new MotTwoWeekEmailNotification(WEB_BASE_URL);
    }
}
