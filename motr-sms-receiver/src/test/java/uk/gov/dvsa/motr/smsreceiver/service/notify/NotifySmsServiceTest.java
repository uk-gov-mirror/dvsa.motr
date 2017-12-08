package uk.gov.dvsa.motr.smsreceiver.service.notify;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.smsreceiver.notify.NotifySmsService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class NotifySmsServiceTest {

    private static final String PHONE_NUMBER = "05000000";
    private static final String VRM = "ASDF123";

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private String smsUnsubscriptionConfirmationTemplateId = "TEMPLATE-UNSUBSCRIPTION-CONFIRMATION";

    private NotifySmsService notifySmsService;

    @Before
    public void setUp() {
        notifySmsService = new NotifySmsService(notificationClient, smsUnsubscriptionConfirmationTemplateId);
    }

    @Test
    public void smsUnsubscriptionConfirmationIsSentWithCorrectDetails() throws NotificationClientException {

        notifySmsService.sendUnsubscriptionConfirmationSms(PHONE_NUMBER, VRM);

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", VRM);

        verify(notificationClient, times(1)).sendSms(
                smsUnsubscriptionConfirmationTemplateId,
                PHONE_NUMBER,
                personalisation,
                ""
        );
    }

    @Test(expected = NotificationClientException.class)
    @UseDataProvider("dataProviderWhenNotificationClientThrowsAnErrorItIsThrown")
    public void whenNotificationClientThrowsAnErrorItIsThrown(String phoneNumber, String vrm)
            throws NotificationClientException {

        when(notificationClient.sendSms(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        notifySmsService.sendUnsubscriptionConfirmationSms(phoneNumber, vrm);
    }

    @DataProvider
    public static Object[][] dataProviderWhenNotificationClientThrowsAnErrorItIsThrown() {
        return new Object[][] {
                { "", "" },
                { PHONE_NUMBER, "" },
                { "", VRM }
        };
    }
}
