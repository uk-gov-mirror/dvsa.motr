package uk.gov.dvsa.motr.smsreceiver.service.notify;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.smsreceiver.notify.NotifySmsService;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
    private NotifyTemplateEngine notifyTemplateEngine = mock(NotifyTemplateEngine.class);

    @Before
    public void setUp() {
        notifySmsService = new NotifySmsService(notificationClient, smsUnsubscriptionConfirmationTemplateId, notifyTemplateEngine);
    }

    @Test
    public void smsUnsubscriptionConfirmationIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {

        notifySmsService.sendUnsubscriptionConfirmationSms(PHONE_NUMBER, VRM, VehicleType.MOT);

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", VRM);

        verify(notifyTemplateEngine, times(1)).getNotifyParameters(any(), eq(personalisation));

        verify(notificationClient, times(1)).sendSms(
                smsUnsubscriptionConfirmationTemplateId,
                PHONE_NUMBER,
                Collections.emptyMap(),
                ""
        );
    }

    @Test(expected = NotificationClientException.class)
    @UseDataProvider("dataProviderWhenNotificationClientThrowsAnErrorItIsThrown")
    public void whenNotificationClientThrowsAnErrorItIsThrown(String phoneNumber, String vrm)
            throws NotificationClientException, NotifyTemplateEngineException {

        when(notificationClient.sendSms(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        notifySmsService.sendUnsubscriptionConfirmationSms(phoneNumber, vrm, VehicleType.MOT);
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
