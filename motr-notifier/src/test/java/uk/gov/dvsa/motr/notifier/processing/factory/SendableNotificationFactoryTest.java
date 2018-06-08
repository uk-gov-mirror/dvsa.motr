package uk.gov.dvsa.motr.notifier.processing.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import uk.gov.dvsa.motr.notifier.helpers.EuRoadworthinessToggle;
import uk.gov.dvsa.motr.notifier.notify.NotificationTemplateIds;
import uk.gov.dvsa.motr.notifier.processing.model.ContactDetail;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.SendableNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.HgvPsvOneMonthEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.HgvPsvTwoMonthEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotOneDayAfterEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotOneMonthEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotTwoWeekEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.HgvPsvOneMonthSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.HgvPsvTwoMonthSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotOneDayAfterSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotOneMonthSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotTwoWeekSmsNotification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.mock;

public class SendableNotificationFactoryTest {
    private SendableNotificationFactory notificationFactory;

    private EuRoadworthinessToggle euRoadworthinessToggle = mock(EuRoadworthinessToggle.class);

    private static final String BASE_URL = "http://gov.uk/";
    private static final String VEHICLE_DATA_URL = "http://gov.uk/mot-history/";
    private static final String CHECKSUM_SALT = "salt";
    private static final String SUBSCRIPTION_ID = "12345";
    private static final String TEST_VRM = "TEST-VRM";
    private static final String MOT_TEST_NUMBER = "test-mot-number-123";
    private static final String TEST_MAKE = "TEST-MAKE";
    private static final String TEST_MODEL = "TEST-MODEL";
    private static final String ONE_MONTH_TEMPLATE_ID = "ONE_MONTH_TEMPLATE_ID";
    private static final String TWO_WEEKS_TEMPLATE_ID = "TWO_WEEKS_TEMPLATE_ID";
    private static final String ONE_DAY_AGO_TEMPLATE_ID = "ONE_DAY_AGO_TEMPLATE_ID";
    private static final String ONE_MONTH_TEMPLATE_ID_POST_EU = "ONE_MONTH_TEMPLATE_ID_POST_EU";
    private static final String TWO_WEEKS_TEMPLATE_ID_POST_EU = "TWO_WEEKS_TEMPLATE_ID_POST_EU";
    private static final String ONE_DAY_AGO_TEMPLATE_ID_POST_EU = "ONE_DAY_AGO_TEMPLATE_ID_POST_EU";
    private static final String ONE_MONTH_HGV_PSV_TEMPLATE_ID = "ONE_MONTH_HGV_PSV_TEMPLATE_ID";
    private static final String TWO_MONTH_HGV_PSV_TEMPLATE_ID = "TWO_MONTH_HGV_PSV_TEMPLATE_ID";
    private static final String SMS_ONE_MONTH_TEMPLATE_ID = "SMS_ONE_MONTH_TEMPLATE_ID";
    private static final String SMS_TWO_WEEKS_TEMPLATE_ID = "SMS_TWO_WEEKS_TEMPLATE_ID";
    private static final String SMS_ONE_DAY_AGO_TEMPLATE_ID = "SMS_ONE_DAY_AGO_TEMPLATE_ID";
    private static final String SMS_ONE_MONTH_HGV_PSV_TEMPLATE_ID = "SMS_ONE_MONTH_HGV_PSV_TEMPLATE_ID";
    private static final String SMS_TWO_MONTH_HGV_PSV_TEMPLATE_ID = "SMS_TWO_MONTH_HGV_PSV_TEMPLATE_ID";

    @Before
    public void setUp() {
        notificationFactory = new SendableNotificationFactory(emailNotificationTemplateIdsStub(), smsNotificationTemplateIdsStub(),
                BASE_URL, VEHICLE_DATA_URL, CHECKSUM_SALT, euRoadworthinessToggle);
    }

    @Test
    public void noEmailNotificationIsCreated_whenDateDoesNotMatchAnyNotification() {
        LocalDate requestDate = LocalDate.of(2016, 2, 3);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        Optional<SendableEmailNotification> notification = notificationFactory.getEmailNotification(
                requestDate, subscription, vehicleDetails);

        Assert.assertFalse(notification.isPresent());
    }

    @Test
    public void whenVehicleExpiryDateIsInOneMonth_EmailNotificationIsCreated_preEu() {
        LocalDate requestDate = LocalDate.of(2018, 9, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        Mockito.when(euRoadworthinessToggle.isEuRoadworthinessLive()).thenReturn(false);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(MotOneMonthEmailNotification.class, notification.getClass());
        Assert.assertEquals(ONE_MONTH_TEMPLATE_ID, notification.getTemplateId());
        Assert.assertNotNull(notification.getPersonalisation().get(MotOneMonthEmailNotification. MOT_EXPIRY_DATE_KEY));
    }

    @Test
    public void whenVehicleExpiryDateIsInOneMonth_EmailNotificationIsCreated_postEu() {
        LocalDate requestDate = LocalDate.of(2018, 9, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        Mockito.when(euRoadworthinessToggle.isEuRoadworthinessLive()).thenReturn(true);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(MotOneMonthEmailNotification.class, notification.getClass());
        Assert.assertEquals(ONE_MONTH_TEMPLATE_ID_POST_EU, notification.getTemplateId());
        Assert.assertNotNull(notification.getPersonalisation().get(MotOneMonthEmailNotification. MOT_EXPIRY_DATE_KEY));
    }

    @Test
    public void whenVehicleExpiryDateIsInTwoWeeks_EmailNotificationIsCreated_preEu() {
        LocalDate requestDate = LocalDate.of(2018, 10, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        Mockito.when(euRoadworthinessToggle.isEuRoadworthinessLive()).thenReturn(false);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(MotTwoWeekEmailNotification.class, notification.getClass());
        Assert.assertEquals(TWO_WEEKS_TEMPLATE_ID, notification.getTemplateId());
        Assert.assertNotNull(notification.getPersonalisation().get(MotTwoWeekEmailNotification.MOT_EXPIRY_DATE_KEY));
    }

    @Test
    public void whenVehicleExpiryDateIsInTwoWeeks_EmailNotificationIsCreated_postEu() {
        LocalDate requestDate = LocalDate.of(2018, 10, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 24);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        Mockito.when(euRoadworthinessToggle.isEuRoadworthinessLive()).thenReturn(true);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(MotTwoWeekEmailNotification.class, notification.getClass());
        Assert.assertEquals(TWO_WEEKS_TEMPLATE_ID_POST_EU, notification.getTemplateId());
        Assert.assertNotNull(notification.getPersonalisation().get(MotOneMonthEmailNotification. MOT_EXPIRY_DATE_KEY));
    }

    @Test
    public void whenVehicleExpiryDateIsOneDayAgo_EmailNotificationIsCreated_preEu() {
        LocalDate requestDate = LocalDate.of(2018, 10, 11);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        Mockito.when(euRoadworthinessToggle.isEuRoadworthinessLive()).thenReturn(false);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(MotOneDayAfterEmailNotification.class, notification.getClass());
        Assert.assertEquals(ONE_DAY_AGO_TEMPLATE_ID, notification.getTemplateId());
    }

    @Test
    public void whenVehicleExpiryDateIsOneDayAgo_EmailNotificationIsCreated_postEu() {
        LocalDate requestDate = LocalDate.of(2018, 10, 11);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        Mockito.when(euRoadworthinessToggle.isEuRoadworthinessLive()).thenReturn(true);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(MotOneDayAfterEmailNotification.class, notification.getClass());
        Assert.assertEquals(ONE_DAY_AGO_TEMPLATE_ID_POST_EU, notification.getTemplateId());
    }

    @Test
    public void whenHgvVehicleExpiryDateIsInOneMonth_HgvPsvEmailNotificationIsCreated() {
        LocalDate requestDate = LocalDate.of(2018, 9, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.PSV)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(HgvPsvOneMonthEmailNotification.class, notification.getClass());
        Assert.assertEquals(ONE_MONTH_HGV_PSV_TEMPLATE_ID, notification.getTemplateId());
    }

    @Test
    public void whenHgvVehicleExpiryDateIsInTwoMonth_HgvPsvEmailNotificationIsCreated() {
        LocalDate requestDate = LocalDate.of(2018, 9, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 11, 9);
        SubscriptionQueueItem subscription = subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.HGV)
                .setMotTestNumber(MOT_TEST_NUMBER);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        SendableNotification notification = notificationFactory.getEmailNotification(requestDate, subscription, vehicleDetails).get();

        Assert.assertEquals(HgvPsvTwoMonthEmailNotification.class, notification.getClass());
        Assert.assertEquals(TWO_MONTH_HGV_PSV_TEMPLATE_ID, notification.getTemplateId());
    }

    @Test
    public void whenHgvVehicleExpiryDateIsInTwoMonth_HgvPsvSmsNotificationIsCreated() {
        LocalDate requestDate = LocalDate.of(2018, 9, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 11, 9);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        SendableNotification notification = notificationFactory.getSmsNotification(
                subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.PSV), vehicleDetails).get();

        Assert.assertEquals(HgvPsvTwoMonthSmsNotification.class, notification.getClass());
        Assert.assertEquals(SMS_TWO_MONTH_HGV_PSV_TEMPLATE_ID, notification.getTemplateId());
    }

    @Test
    public void whenHgvVehicleExpiryDateIsInOneMonth_HgvPsvSmsNotificationIsCreated() {
        LocalDate requestDate = LocalDate.of(2018, 9, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate).setVehicleType(VehicleType.PSV);

        SendableNotification notification = notificationFactory.getSmsNotification(
                subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.HGV), vehicleDetails).get();

        Assert.assertEquals(HgvPsvOneMonthSmsNotification.class, notification.getClass());
        Assert.assertEquals(SMS_ONE_MONTH_HGV_PSV_TEMPLATE_ID, notification.getTemplateId());
    }

    @Test
    public void whenMotVehicleExpiryDateIsInOneMonth_SmsNotificationIsCreated() {
        LocalDate requestDate = LocalDate.of(2018, 9, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 10);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        SendableNotification notification = notificationFactory.getSmsNotification(
                subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT), vehicleDetails).get();

        Assert.assertEquals(MotOneMonthSmsNotification.class, notification.getClass());
        Assert.assertEquals(SMS_ONE_MONTH_TEMPLATE_ID, notification.getTemplateId());
    }

    @Test
    public void whenMotVehicleExpiryDateIsInTwoWeeks_SmsNotificationIsCreated() {
        LocalDate requestDate = LocalDate.of(2018, 10, 10);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 24);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        SendableNotification notification = notificationFactory.getSmsNotification(
                subscriptionStub(vehicleExpiryDate, requestDate, VehicleType.MOT), vehicleDetails).get();

        Assert.assertEquals(MotTwoWeekSmsNotification.class, notification.getClass());
        Assert.assertEquals(SMS_TWO_WEEKS_TEMPLATE_ID, notification.getTemplateId());
    }

    @Test
    public void whenMotVehicleExpiryDateIsOneDayAgo_SmsNotificationIsCreated() {
        LocalDate requestDate = LocalDate.of(2018, 10, 25);
        LocalDate vehicleExpiryDate = LocalDate.of(2018, 10, 24);
        VehicleDetails vehicleDetails = vehicleDetailsStub(vehicleExpiryDate);

        SendableNotification notification = notificationFactory.getSmsNotification(
                subscriptionStub(requestDate, requestDate, VehicleType.MOT), vehicleDetails).get();

        Assert.assertEquals(MotOneDayAfterSmsNotification.class, notification.getClass());
        Assert.assertEquals(SMS_ONE_DAY_AGO_TEMPLATE_ID, notification.getTemplateId());
    }

    private SubscriptionQueueItem subscriptionStub(LocalDate motDueDate, LocalDate requestDate, VehicleType vehicleType) {

        return new SubscriptionQueueItem()
                .setId(SUBSCRIPTION_ID)
                .setMessageReceiptHandle("Test-receipt-handle")
                .setContactDetail(new ContactDetail("test@this-is-a-test-123", SubscriptionQueueItem.ContactType.EMAIL))
                .setMotDueDate(motDueDate)
                .setLoadedOnDate(requestDate)
                .setVrm(TEST_VRM)
                .setVehicleType(vehicleType);
    }

    private VehicleDetails vehicleDetailsStub(LocalDate expiryDate) {
        return new VehicleDetails()
                .setMotExpiryDate(expiryDate)
                .setRegNumber(TEST_VRM)
                .setMake(TEST_MAKE)
                .setModel(TEST_MODEL)
                .setVehicleType(VehicleType.MOT);
    }

    private NotificationTemplateIds emailNotificationTemplateIdsStub() {
        return new NotificationTemplateIds()
                .setOneDayAfterNotificationTemplateIdPreEu(ONE_DAY_AGO_TEMPLATE_ID)
                .setTwoWeekNotificationTemplateIdPreEu(TWO_WEEKS_TEMPLATE_ID)
                .setOneMonthNotificationTemplateIdPreEu(ONE_MONTH_TEMPLATE_ID)
                .setOneDayAfterNotificationTemplateId(ONE_DAY_AGO_TEMPLATE_ID_POST_EU)
                .setTwoWeekNotificationTemplateId(TWO_WEEKS_TEMPLATE_ID_POST_EU)
                .setOneMonthNotificationTemplateId(ONE_MONTH_TEMPLATE_ID_POST_EU)
                .setOneMonthHgvPsvNotificationTemplateId(ONE_MONTH_HGV_PSV_TEMPLATE_ID)
                .setTwoMonthHgvPsvNotificationTemplateId(TWO_MONTH_HGV_PSV_TEMPLATE_ID);
    }

    private NotificationTemplateIds smsNotificationTemplateIdsStub() {
        return new NotificationTemplateIds()
                .setOneDayAfterNotificationTemplateId(SMS_ONE_DAY_AGO_TEMPLATE_ID)
                .setTwoWeekNotificationTemplateId(SMS_TWO_WEEKS_TEMPLATE_ID)
                .setOneMonthNotificationTemplateId(SMS_ONE_MONTH_TEMPLATE_ID)
                .setOneMonthHgvPsvNotificationTemplateId(SMS_ONE_MONTH_HGV_PSV_TEMPLATE_ID)
                .setTwoMonthHgvPsvNotificationTemplateId(SMS_TWO_MONTH_HGV_PSV_TEMPLATE_ID);
    }
}
