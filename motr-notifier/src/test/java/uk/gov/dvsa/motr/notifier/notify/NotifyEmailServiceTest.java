package uk.gov.dvsa.motr.notifier.notify;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.SendableNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.HgvPsvOneMonthEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.HgvPsvTwoMonthEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotOneDayAfterEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotOneMonthEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.MotTwoWeekEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyEmailServiceTest {

    private static final String HGV_PSV_DIRECTORY = "hgv-psv/";
    private static final String EMAIL = "test@test.com";
    private static final String REG = "TEST-REG";
    private static final String MAKE = "TEST-MAKE";
    private static final String MODEL = "TEST-MODEL";
    private static final String CHECKSUM = "checksumSalt";
    private static final String UNSUBSCRIBE_LINK = "http://unsubscribe.uk";
    private static final LocalDate EXPIRY_DATE = LocalDate.of(2017, 10, 10);
    private static final String MOT_PRESERVATION_STATEMENT_PREFIX =
            "You can get your MOT test done from tomorrow to keep the same MOT test date ";
    private static final String HGV_PSV_PRESERVATION_STATEMENT_PREFIX =
            "You can get your annual test done from tomorrow to keep the same expiry date ";
    private static final String PRESERVATION_STATEMENT_SUFFIX = " for next year.";
    private static final String MOTH_DIRECT_URL_PREFIX = "http://mot-history.uk/";

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private NotificationTemplateIds notificationTemplateIds = new NotificationTemplateIds()
            .setOneMonthNotificationTemplateIdPreEu("TEMPLATE-ONE-MONTH")
            .setOneMonthNotificationTemplateIdPreEu("TEMPLATE-TWO-MONTH-HGV-PSV")
            .setTwoWeekNotificationTemplateIdPreEu("TEMPLATE-TWO-WEEK")
            .setOneDayAfterNotificationTemplateIdPreEu("TEMPLATE-ONE-DAY-AFTER")
            .setOneMonthNotificationTemplateId("TEMPLATE-ONE-MONTH-POST-EU")
            .setTwoWeekNotificationTemplateId("TEMPLATE-TWO-WEEK-POST-EU")
            .setOneDayAfterNotificationTemplateId("TEMPLATE-ONE-DAY-AFTER-POST-EU");

    private NotifyEmailService notifyEmailService;
    private NotifyTemplateEngine notifyTemplateEngine = mock(NotifyTemplateEngine.class);
    private Map<String, String> body = new HashMap<>();

    @Before
    public void setUp() {
        notifyEmailService = new NotifyEmailService(notificationClient, notifyTemplateEngine);

        body.put("body", "This is the body");

        try {
            when(notifyTemplateEngine.getNotifyParameters(any(), any())).thenReturn(body);
            when(notifyTemplateEngine.getNotifyParameters(any(), any(), any())).thenReturn(body);

        } catch (NotifyTemplateEngineException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void oneMonthNotificationIsSentWithCorrectDetails_whenMotTestNumber() throws NotificationClientException,
            NotifyTemplateEngineException, NoSuchAlgorithmException {

        SendableEmailNotification notification = new MotOneMonthEmailNotification(UNSUBSCRIBE_LINK, MOTH_DIRECT_URL_PREFIX, CHECKSUM)
                .setTemplateId(notificationTemplateIds.getOneMonthNotificationTemplateIdPreEu());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE).setMotTestNumber("123123");

        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetails);

        StringBuilder preservationStatementSb = new StringBuilder(128)
                .append(MOT_PRESERVATION_STATEMENT_PREFIX)
                .append(DateFormatterForEmailDisplay.asFormattedForEmailDateWithoutYear(EXPIRY_DATE))
                .append(PRESERVATION_STATEMENT_SUFFIX);

        Map<String, String> personalisation = stubGenericPersonalisation(notification);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "expires");
        personalisation.put("preservation_statement", preservationStatementSb.toString());
        personalisation.put("moth_url", MotHistoryUrlFormatter.getUrl(MOTH_DIRECT_URL_PREFIX, vehicleDetails, CHECKSUM));

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq("one-month-notification-email-subject.txt"), eq("one-month-notification-email-body.txt"), eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getOneMonthNotificationTemplateIdPreEu(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void oneMonthNotificationIsSentWithCorrectDetails_whenNoMotTest() throws NotificationClientException,
            NotifyTemplateEngineException, NoSuchAlgorithmException {

        SendableEmailNotification notification = new MotOneMonthEmailNotification(UNSUBSCRIBE_LINK, MOTH_DIRECT_URL_PREFIX, CHECKSUM)
                .setTemplateId(notificationTemplateIds.getOneMonthNotificationTemplateIdPreEu());


        notification.personalise(stubSubscriptionQueueItem(), vehicleDetailsStub(EXPIRY_DATE));
        Map<String, String> personalisation = stubGenericPersonalisation(notification);

        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetailsStub(EXPIRY_DATE));

        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "is due");
        personalisation.put("preservation_statement", "");
        personalisation.put("moth_url", MotHistoryUrlFormatter.getUrl(MOTH_DIRECT_URL_PREFIX, vehicleDetailsStub(EXPIRY_DATE), CHECKSUM));

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq("one-month-notification-email-subject.txt"), eq("one-month-notification-email-body.txt"), eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getOneMonthNotificationTemplateIdPreEu(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void twoMonthNotificationIsSentForHgvPsv_whenMotTestNumber() throws NotificationClientException, NotifyTemplateEngineException,
            NoSuchAlgorithmException {

        SendableEmailNotification notification = new HgvPsvTwoMonthEmailNotification(UNSUBSCRIBE_LINK, MOTH_DIRECT_URL_PREFIX, CHECKSUM)
                .setTemplateId(notificationTemplateIds.getTwoMonthHgvPsvNotificationTemplateId());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE).setVehicleType(VehicleType.HGV).setMotTestNumber("123123");
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        Map<String, String> personalisation = stubGenericPersonalisation(notification);

        StringBuilder preservationStatementSb = new StringBuilder(128)
                .append(HGV_PSV_PRESERVATION_STATEMENT_PREFIX)
                .append(DateFormatterForEmailDisplay.asFormattedForEmailDateWithoutYear(EXPIRY_DATE))
                .append(PRESERVATION_STATEMENT_SUFFIX);

        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "expires");
        personalisation.put("preservation_statement", preservationStatementSb.toString());
        personalisation.put("moth_url", MotHistoryUrlFormatter.getUrl(MOTH_DIRECT_URL_PREFIX, vehicleDetails, CHECKSUM));

        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetails);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq(HGV_PSV_DIRECTORY + "hgv-psv-two-month-notification-email-subject.txt"),
                eq(HGV_PSV_DIRECTORY + "hgv-psv-two-month-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getTwoMonthHgvPsvNotificationTemplateId(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void oneMonthNotificationIsSentForHgvPsv_whenMotTestNumber() throws NotificationClientException, NotifyTemplateEngineException {

        SendableEmailNotification notification = new HgvPsvOneMonthEmailNotification(UNSUBSCRIBE_LINK)
                .setTemplateId(notificationTemplateIds.getOneMonthHgvPsvNotificationTemplateId());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE).setVehicleType(VehicleType.HGV).setMotTestNumber("123123");
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        Map<String, String> personalisation = stubGenericPersonalisation(notification);

        StringBuilder preservationStatementSb = new StringBuilder(128)
                .append(HGV_PSV_PRESERVATION_STATEMENT_PREFIX)
                .append(DateFormatterForEmailDisplay.asFormattedForEmailDateWithoutYear(EXPIRY_DATE))
                .append(PRESERVATION_STATEMENT_SUFFIX);

        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "expires");
        personalisation.put("preservation_statement", preservationStatementSb.toString());

        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetails);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq(HGV_PSV_DIRECTORY + "hgv-psv-one-month-notification-email-subject.txt"),
                eq(HGV_PSV_DIRECTORY + "hgv-psv-one-month-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getOneMonthHgvPsvNotificationTemplateId(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void oneMonthNotificationIsSentForHgvPsv_whenNoMotTest() throws NotificationClientException, NotifyTemplateEngineException {

        SendableEmailNotification notification = new HgvPsvOneMonthEmailNotification(UNSUBSCRIBE_LINK)
                .setTemplateId(notificationTemplateIds.getOneMonthHgvPsvNotificationTemplateId());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE).setVehicleType(VehicleType.PSV);
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        Map<String, String> personalisation = stubGenericPersonalisation(notification);

        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "is due");
        personalisation.put("preservation_statement", "");

        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetails);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq(HGV_PSV_DIRECTORY + "hgv-psv-one-month-notification-email-subject.txt"),
                eq(HGV_PSV_DIRECTORY + "hgv-psv-one-month-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getTwoMonthHgvPsvNotificationTemplateId(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void twoMonthNotificationIsSentForHgvPsv_whenNoMotTest() throws NotificationClientException, NotifyTemplateEngineException,
            NoSuchAlgorithmException {

        SendableEmailNotification notification = new HgvPsvTwoMonthEmailNotification(UNSUBSCRIBE_LINK, MOTH_DIRECT_URL_PREFIX, CHECKSUM)
                .setTemplateId(notificationTemplateIds.getTwoMonthHgvPsvNotificationTemplateId());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE).setVehicleType(VehicleType.PSV);
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        Map<String, String> personalisation = stubGenericPersonalisation(notification);

        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "is due");
        personalisation.put("preservation_statement", "");
        personalisation.put("moth_url", MotHistoryUrlFormatter.getUrl(MOTH_DIRECT_URL_PREFIX, vehicleDetails, CHECKSUM));

        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetails);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq(HGV_PSV_DIRECTORY + "hgv-psv-two-month-notification-email-subject.txt"),
                eq(HGV_PSV_DIRECTORY + "hgv-psv-two-month-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getTwoMonthHgvPsvNotificationTemplateId(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void twoWeekNotificationIsSentWithCorrectDetails_whenMotTestNumber() throws NotificationClientException,
            NotifyTemplateEngineException {

        SendableEmailNotification notification = new MotTwoWeekEmailNotification(UNSUBSCRIBE_LINK)
                .setTemplateId(notificationTemplateIds.getTwoWeekNotificationTemplateIdPreEu());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE).setMotTestNumber("123123");
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetails);

        Map<String, String> personalisation = stubGenericPersonalisation(notification);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "expires");

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq("two-week-notification-email-subject.txt"),
                eq("two-week-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getTwoWeekNotificationTemplateIdPreEu(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void twoWeekNotificationIsSentWithCorrectDetails_whenNoMotTest()
            throws NotificationClientException, NotifyTemplateEngineException {
        SendableEmailNotification notification = new MotTwoWeekEmailNotification(UNSUBSCRIBE_LINK)
                .setTemplateId(notificationTemplateIds.getTwoWeekNotificationTemplateIdPreEu());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE);
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetailsStub(EXPIRY_DATE));

        Map<String, String> personalisation = stubGenericPersonalisation(notification);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "is due");

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq("two-week-notification-email-subject.txt"),
                eq("two-week-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getTwoWeekNotificationTemplateIdPreEu(),
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void oneDayAfterNotificationIsSentWithCorrectDetails_whenMotTestNumber()
            throws NotificationClientException, NotifyTemplateEngineException {

        SendableEmailNotification notification = new MotOneDayAfterEmailNotification(UNSUBSCRIBE_LINK)
                .setTemplateId(notificationTemplateIds.getOneDayAfterNotificationTemplateIdPreEu());
        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE).setMotTestNumber("123123");
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetails);

        Map<String, String> personalisation = stubGenericPersonalisation(notification);
        personalisation.put("was_due_or_expired", "expired");

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq("one-day-after-notification-email-subject.txt"),
                eq("one-day-after-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getOneDayAfterNotificationTemplateIdPreEu(),
                EMAIL,
                body,
                "");
    }

    @Test
    public void oneDayAfterNotificationIsSentWithCorrectDetails_whenNoMotTest()
            throws NotificationClientException, NotifyTemplateEngineException {


        SendableEmailNotification notification = new MotOneDayAfterEmailNotification(UNSUBSCRIBE_LINK)
                .setTemplateId(notificationTemplateIds.getOneDayAfterNotificationTemplateIdPreEu());

        VehicleDetails vehicleDetails = vehicleDetailsStub(EXPIRY_DATE);
        notification.personalise(stubSubscriptionQueueItem(), vehicleDetails);
        notifyEmailService.sendEmail(EMAIL, notification, vehicleDetailsStub(EXPIRY_DATE));

        Map<String, String> personalisation = stubGenericPersonalisation(notification);
        personalisation.put("was_due_or_expired", "was due");

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq("one-day-after-notification-email-subject.txt"),
                eq("one-day-after-notification-email-body.txt"),
                eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                notificationTemplateIds.getOneDayAfterNotificationTemplateIdPreEu(),
                EMAIL,
                body,
                "");
    }

    @Test(expected = NotificationClientException.class)
    public void whenClientThrowsAnErrorItIsThrown() throws NotificationClientException {

        when(notificationClient.sendEmail(any(String.class), any(), any(), any())).thenThrow(NotificationClientException.class);

        notifyEmailService.sendEmail(EMAIL, mock(SendableEmailNotification.class), mock(VehicleDetails.class));
    }

    private Map<String, String> stubGenericPersonalisation(SendableNotification notification) {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("unsubscribe_link", notification.getPersonalisation().get("unsubscribe_link"));
        personalisation.put("vehicle_details", notification.getPersonalisation().get("vehicle_details"));

        return personalisation;
    }

    private VehicleDetails vehicleDetailsStub(LocalDate expiryDate) {
        return new VehicleDetails()
                .setMotExpiryDate(expiryDate)
                .setRegNumber(REG)
                .setMake(MAKE)
                .setModel(MODEL)
                .setVehicleType(VehicleType.MOT);
    }

    private SubscriptionQueueItem stubSubscriptionQueueItem() {
        SubscriptionQueueItem subscription = new SubscriptionQueueItem();
        subscription.setId("a");
        return subscription;
    }
}
