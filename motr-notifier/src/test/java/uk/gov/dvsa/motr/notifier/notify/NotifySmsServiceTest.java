package uk.gov.dvsa.motr.notifier.notify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifySmsServiceTest {

    private static final String PHONE_NUMBER = "0700000000";
    private static final String REG = "TEST-REG";
    private static final LocalDate EXPIRY_DATE = LocalDate.of(2017, 10, 10);

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private String oneMonthNotificationTemplateId = "TEMPLATE-ONE-MONTH";
    private String twoWeekNotificationTemplateId = "TEMPLATE-TWO-WEEK";
    private String oneDayAfterNotificationTemplateId = "TEMPLATE-ONE-DAY-AFTER";
    private String oneMonthNotificationTemplateIdPostEu = "TEMPLATE-ONE-MONTH-POST-EU";
    private String twoWeekNotificationTemplateIdPostEu = "TEMPLATE-TWO-WEEK-POST-EU";
    private String oneDayAfterNotificationTemplateIdPostEu = "TEMPLATE-ONE-DAY-AFTER-POST-EU";

    private NotifySmsService notifySmsService;
    private String euGoLiveDate = "2018-05-20";

    private NotifyTemplateEngine notifyTemplateEngine = mock(NotifyTemplateEngine.class);
    private Map<String, String> body = new HashMap<>();

    @Before
    public void setUp() {
        notifySmsService = new NotifySmsService(notificationClient, oneMonthNotificationTemplateId, twoWeekNotificationTemplateId,
                oneDayAfterNotificationTemplateId, oneMonthNotificationTemplateIdPostEu, twoWeekNotificationTemplateIdPostEu,
                oneDayAfterNotificationTemplateIdPostEu, euGoLiveDate, notifyTemplateEngine);
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

        notifySmsService.sendOneMonthNotificationSms(PHONE_NUMBER, REG, EXPIRY_DATE);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), Matchers.eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                oneMonthNotificationTemplateId,
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
        notifySmsService.sendTwoWeekNotificationSms(PHONE_NUMBER, REG, EXPIRY_DATE);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), Matchers.eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                twoWeekNotificationTemplateId,
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void oneDayAfterNotificationIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {
        
        Map<String, String> personalisation = stubGenericPersonalisation();

        notifySmsService.sendOneDayAfterNotificationSms(PHONE_NUMBER, REG);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), Matchers.eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                oneDayAfterNotificationTemplateId,
                PHONE_NUMBER,
                body,
                "");
    }

    @Test(expected = NotificationClientException.class)
    public void whenClientThrowsAnErrorItIsThrown()
            throws NotificationClientException, NotifyTemplateEngineException {

        when(notificationClient.sendSms(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        notifySmsService.sendOneMonthNotificationSms(
                "",
                "",
                LocalDate.of(2017, 10, 10)
        );
    }

    private Map<String, String> stubGenericPersonalisation() {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", REG);
        return personalisation;
    }

    @Test
    public void testGetEuRoadworthinessReturnsTrue_WhenCurrentDateIsGreaterThanGoLiveDate() {

        String euGoLiveDate = "2018-01-20";
        boolean isEuRoadworthinessLive = this.notifySmsService.isEuRoadworthinessLive(euGoLiveDate);

        assertTrue(isEuRoadworthinessLive);
    }

    @Test
    public void testGetEuRoadworthinessReturnsFalse_WhenCurrentDateIsLessThanGoLiveDate() {

        String euGoLiveDate = "2019-05-20";
        boolean isEuRoadworthinessLive = this.notifySmsService.isEuRoadworthinessLive(euGoLiveDate);

        assertFalse(isEuRoadworthinessLive);
    }
}
