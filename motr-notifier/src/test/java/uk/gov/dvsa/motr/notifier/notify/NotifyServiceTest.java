package uk.gov.dvsa.motr.notifier.notify;

import org.junit.Before;
import org.junit.Test;

import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyServiceTest {

    private static final String EMAIL = "test@test.com";
    private static final String REG = "TEST-REG";
    private static final String UNSUBSCRIBE_LINK = "http://gov.uk";
    private static final LocalDate EXPIRY_DATE = LocalDate.of(2017, 10, 10);

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private String oneMonthNotificationTemplateId = "TEMPLATE-ONE-MONTH";
    private String twoWeekNotificationTemplateId = "TEMPLATE-TWO-WEEK";
    private String oneDayAfterNotificationTemplateId = "TEMPLATE-ONE-DAY-AFTER";

    private NotifyService notifyService;

    @Before
    public void setUp() {
        notifyService = new NotifyService(notificationClient, oneMonthNotificationTemplateId, twoWeekNotificationTemplateId,
                oneDayAfterNotificationTemplateId);
    }

    @Test
    public void oneMonthNotificationIsSentWithCorrectDetails() throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("registration_number", REG);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("unsubscribe_link", UNSUBSCRIBE_LINK);

        notifyService.sendOneMonthNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK);

        verify(notificationClient, times(1)).sendEmail(oneMonthNotificationTemplateId, EMAIL, personalisation, "");
    }

    @Test
    public void twoWeekNotificationIsSentWithCorrectDetails() throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("registration_number", REG);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("unsubscribe_link", UNSUBSCRIBE_LINK);

        notifyService.sendTwoWeekNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK);

        verify(notificationClient, times(1)).sendEmail(twoWeekNotificationTemplateId, EMAIL, personalisation, "");
    }

    @Test
    public void oneDayAfterNotificationIsSentWithCorrectDetails() throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("registration_number", REG);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(EXPIRY_DATE));
        personalisation.put("unsubscribe_link", UNSUBSCRIBE_LINK);

        notifyService.sendOneDayAfterNotificationEmail(EMAIL, REG, EXPIRY_DATE, UNSUBSCRIBE_LINK);

        verify(notificationClient, times(1)).sendEmail(oneDayAfterNotificationTemplateId, EMAIL, personalisation, "");
    }

    @Test(expected = NotificationClientException.class)
    public void whenClientThrowsAnErrorItIsThrown() throws NotificationClientException {

        when(notificationClient.sendEmail(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        notifyService.sendOneMonthNotificationEmail("","",LocalDate.of(2017, 10, 10), "");
    }

}
