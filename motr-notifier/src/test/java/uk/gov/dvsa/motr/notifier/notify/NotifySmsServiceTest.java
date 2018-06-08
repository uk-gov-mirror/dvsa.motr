package uk.gov.dvsa.motr.notifier.notify;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.HgvPsvOneMonthSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.HgvPsvTwoMonthSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotOneDayAfterSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotOneMonthSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.MotTwoWeekSmsNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.SendableSmsNotification;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifySmsServiceTest {

    private static final String PHONE_NUMBER = "0700000000";
    private static final String REG = "TEST-REG";
    private static final String HGV_PSV_DIRECTORY = "hgv-psv/";
    private static final LocalDate EXPIRY_DATE = LocalDate.of(2017, 10, 10);

    private NotificationClient notificationClient = mock(NotificationClient.class);

    private NotificationTemplateIds notificationTemplateIds = new NotificationTemplateIds()
            .setTwoMonthHgvPsvNotificationTemplateId("TEMPLATE-TWO-MONTH-HGV-PSV")
            .setOneMonthHgvPsvNotificationTemplateId("TEMPLATE-ONE-MONTH-HGV-PSV")
            .setOneMonthNotificationTemplateId("TEMPLATE-ONE-MONTH")
            .setTwoWeekNotificationTemplateId("TEMPLATE-TWO-WEEK")
            .setOneDayAfterNotificationTemplateId("TEMPLATE-ONE-DAY-AFTER");

    private NotifySmsService notifySmsService;

    private NotifyTemplateEngine notifyTemplateEngine = mock(NotifyTemplateEngine.class);
    private Map<String, String> body = new HashMap<>();

    @Before
    public void setUp() {
        notifySmsService = new NotifySmsService(notificationClient, notifyTemplateEngine);
        body.put("body", "This is the body");
        try {
            when(notifyTemplateEngine.getNotifyParameters(any(), any())).thenReturn(body);
            when(notifyTemplateEngine.getNotifyParameters(any(), any(), any())).thenReturn(body);

        } catch (NotifyTemplateEngineException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void oneMonthNotificationIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(EXPIRY_DATE));

        SendableSmsNotification notification = new MotOneMonthSmsNotification()
                .setTemplateId(notificationTemplateIds.getOneMonthNotificationTemplateId());

        notification.personalise(stubSubscriptionQueueItem());

        notifySmsService.sendSms(PHONE_NUMBER, notification);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(eq("one-month-notification-sms.txt"), eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                notificationTemplateIds.getOneMonthNotificationTemplateId(),
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void twoWeekNotificationIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(EXPIRY_DATE));

        SendableSmsNotification notification = new MotTwoWeekSmsNotification()
                .setTemplateId(notificationTemplateIds.getTwoWeekNotificationTemplateId());

        notification.personalise(stubSubscriptionQueueItem());

        notifySmsService.sendSms(PHONE_NUMBER, notification);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(eq("two-week-notification-sms.txt"), eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                notificationTemplateIds.getTwoWeekNotificationTemplateId(),
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void oneDayAfterNotificationIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();

        SendableSmsNotification notification = new MotOneDayAfterSmsNotification()
                .setTemplateId(notificationTemplateIds.getOneDayAfterNotificationTemplateId());

        notification.personalise(stubSubscriptionQueueItem());

        notifySmsService.sendSms(PHONE_NUMBER, notification);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(eq("one-day-after-notification-sms.txt"), eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                notificationTemplateIds.getOneDayAfterNotificationTemplateId(),
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void twoMonthHgvPsvNotificationIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(EXPIRY_DATE));

        SendableSmsNotification notification = new HgvPsvTwoMonthSmsNotification()
                .setTemplateId(notificationTemplateIds.getTwoMonthHgvPsvNotificationTemplateId());

        notification.personalise(stubSubscriptionQueueItem());

        notifySmsService.sendSms(PHONE_NUMBER, notification);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq(HGV_PSV_DIRECTORY + "hgv-psv-notification-sms.txt"), eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                notificationTemplateIds.getTwoMonthHgvPsvNotificationTemplateId(),
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void oneMonthHgvPsvNotificationIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(EXPIRY_DATE));

        SendableSmsNotification notification = new HgvPsvOneMonthSmsNotification()
                .setTemplateId(notificationTemplateIds.getOneMonthHgvPsvNotificationTemplateId());

        notification.personalise(stubSubscriptionQueueItem());

        notifySmsService.sendSms(PHONE_NUMBER, notification);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(
                eq(HGV_PSV_DIRECTORY + "hgv-psv-notification-sms.txt"), eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                notificationTemplateIds.getOneMonthHgvPsvNotificationTemplateId(),
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test(expected = NotificationClientException.class)
    public void whenClientThrowsAnErrorItIsThrown()
            throws NotificationClientException {

        when(notificationClient.sendSms(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        notifySmsService.sendSms(
                PHONE_NUMBER,
                new MotOneMonthSmsNotification()
        );
    }

    private Map<String, String> stubGenericPersonalisation() {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", REG);
        return personalisation;
    }

    private SubscriptionQueueItem stubSubscriptionQueueItem() {
        SubscriptionQueueItem subscription = new SubscriptionQueueItem();
        subscription.setId("a")
                .setVrm(REG)
                .setMotDueDate(EXPIRY_DATE);
        return subscription;
    }
}
