package uk.gov.dvsa.motr.notifier.notify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.security.NoSuchAlgorithmException;
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

public class NotifyEmailServiceTest {

    private static final String EMAIL = "test@test.com";
    private static final String REG = "TEST-REG";
    private static final String UNSUBSCRIBE_LINK = "http://gov.uk";
    private static final LocalDate EXPIRY_DATE = LocalDate.of(2017, 10, 10);
    private static final String PRESERVATION_STATEMENT_PREFIX =
            "You can get your MOT test done from tomorrow to keep the same MOT test date ";
    private static final String PRESERVATION_STATEMENT_SUFFIX = " for next year.";
    private static final String MOTH_DIRECT_URL_PREFIX = "http://gov.uk/";

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private String oneMonthNotificationTemplateId = "TEMPLATE-ONE-MONTH";
    private String twoWeekNotificationTemplateId = "TEMPLATE-TWO-WEEK";
    private String oneDayAfterNotificationTemplateId = "TEMPLATE-ONE-DAY-AFTER";
    private String oneMonthNotificationTemplateIdPostEu = "TEMPLATE-ONE-MONTH-POST-EU";
    private String twoWeekNotificationTemplateIdPostEu = "TEMPLATE-TWO-WEEK-POST-EU";
    private String oneDayAfterNotificationTemplateIdPostEu = "TEMPLATE-ONE-DAY-AFTER-POST-EU";

    private String euGoLiveDate = "2018-06-20";
    private NotifyEmailService notifyEmailService;
    private NotifyTemplateEngine notifyTemplateEngine = mock(NotifyTemplateEngine.class);
    private Map<String, String> body = new HashMap<>();

    @Before
    public void setUp() {
        notifyEmailService = new NotifyEmailService(notificationClient, oneMonthNotificationTemplateId, twoWeekNotificationTemplateId,
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
    public void oneMonthNotificationIsSentWithCorrectDetails_whenMotTestNumber()
            throws NotificationClientException, NotifyTemplateEngineException {

        StringBuilder preservationStatementSb = new StringBuilder(128)
                .append(PRESERVATION_STATEMENT_PREFIX)
                .append(DateFormatterForEmailDisplay.asFormattedForEmailDateWithoutYear(EXPIRY_DATE))
                .append(PRESERVATION_STATEMENT_SUFFIX);

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "expires");
        personalisation.put("preservation_statement", preservationStatementSb.toString());
        personalisation.put("moth_url", MOTH_DIRECT_URL_PREFIX);

        notifyEmailService.sendOneMonthNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK, "123123", MOTH_DIRECT_URL_PREFIX);
        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), any(), Matchers.eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                oneMonthNotificationTemplateId,
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void oneMonthNotificationIsSentWithCorrectDetails_whenNoMotTest()
            throws NotificationClientException, NoSuchAlgorithmException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "is due");
        personalisation.put("preservation_statement", "");
        personalisation.put("moth_url", MOTH_DIRECT_URL_PREFIX);
        notifyEmailService.sendOneMonthNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK, "", MOTH_DIRECT_URL_PREFIX);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), any(), Matchers.eq(personalisation));
        verify(notificationClient, times(1)).sendEmail(
                oneMonthNotificationTemplateId,
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void twoWeekNotificationIsSentWithCorrectDetails_whenMotTestNumber()
            throws NotificationClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "expires");
        notifyEmailService.sendTwoWeekNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK, "123123");

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), any(), Matchers.eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                twoWeekNotificationTemplateId,
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void twoWeekNotificationIsSentWithCorrectDetails_whenNoMotTest()
            throws NotificationClientException, NotifyTemplateEngineException {


        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("is_due_or_expires", "is due");

        notifyEmailService.sendTwoWeekNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK, "");

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), any(), Matchers.eq(personalisation));
        verify(notificationClient, times(1)).sendEmail(
                twoWeekNotificationTemplateId,
                EMAIL,
                body,
                ""
        );
    }

    @Test
    public void oneDayAfterNotificationIsSentWithCorrectDetails_whenMotTestNumber()
            throws NotificationClientException, NotifyTemplateEngineException {


        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("was_due_or_expired", "expired");
        notifyEmailService.sendOneDayAfterNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK, "123123");
        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), any(), Matchers.eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                oneDayAfterNotificationTemplateId,
                EMAIL,
                body,
                "");
    }

    @Test
    public void oneDayAfterNotificationIsSentWithCorrectDetails_whenNoMotTest()
            throws NotificationClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubGenericPersonalisation();
        personalisation.put("was_due_or_expired", "was due");

        notifyEmailService.sendOneDayAfterNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK, "");

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), any(), Matchers.eq(personalisation));

        verify(notificationClient, times(1)).sendEmail(
                oneDayAfterNotificationTemplateId,
                EMAIL,
                body,
                "");
    }

    @Test(expected = NotificationClientException.class)
    public void whenClientThrowsAnErrorItIsThrown() throws NotificationClientException {

        when(notificationClient.sendEmail(any(String.class), any(), any(), any())).thenThrow(NotificationClientException.class);

        notifyEmailService.sendOneMonthNotificationEmail(
                "",
                "",
                LocalDate.of(2017, 10, 10),
                "",
                "",
                MOTH_DIRECT_URL_PREFIX
        );
    }

    private Map<String, String> stubGenericPersonalisation() {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_details", REG);
        personalisation.put("unsubscribe_link", UNSUBSCRIBE_LINK);

        return personalisation;
    }

    @Test
    public void testGetEuRoadworthinessReturnsTrue_WhenCurrentDateIsGreaterThanGoLiveDate() {

        String euGoLiveDate = "2018-01-20";
        boolean isEuRoadworthinessLive = this.notifyEmailService.isEuRoadworthinessLive(euGoLiveDate);

        assertTrue(isEuRoadworthinessLive);
    }

    @Test
    public void testGetEuRoadworthinessReturnsFalse_WhenCurrentDateIsLessThanGoLiveDate() {

        String euGoLiveDate = "2019-05-20";
        boolean isEuRoadworthinessLive = this.notifyEmailService.isEuRoadworthinessLive(euGoLiveDate);

        assertFalse(isEuRoadworthinessLive);
    }
}
