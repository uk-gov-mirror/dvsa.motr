package uk.gov.dvsa.motr.notifications.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.formatting.DateFormatter;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyServiceTest {

    private NotifyService service;

    private static final NotificationClient CLIENT = mock(NotificationClient.class);
    private static final String MOT_TEST_NUMBER = "12345";
    private static final String PHONE_NUMBER = "07912345678";
    private static final String CONFIRMATION_CODE = "123456";
    private static final String VRM = "ABC123";

    private String email = "test@test.com";
    private String templateId = "180";
    private String emailConfirmationTemplateId = "180";
    private String smsSubscriptionConfirmationTemplateId = "SMS-SUBSCRIPTION-CONFIRMATION-TEMPLATE";
    private String smsConfirmationTemplateId = "SMS-CONFIRMATION-TEMPLATE";
    private String vehicleDetails = "TEST-MAKE TEST-MODEL, TEST-REG";
    private LocalDate motExpiryDate = LocalDate.of(2017, 1, 1);
    private String unsubscribeLink = "https://gov.uk";

    @Before
    public void setUp() {

        this.service = new NotifyService(CLIENT,
                templateId,
                emailConfirmationTemplateId,
                smsSubscriptionConfirmationTemplateId,
                smsConfirmationTemplateId);
    }

    @Test
    public void notifyCalledWithCorrectValues() throws NotificationClientException {

        Map<String, String> personalisationMap = stubPersonalisationMap(vehicleDetails, motExpiryDate, unsubscribeLink);

        when(CLIENT.sendEmail(any(), any(), any(), any())).thenReturn(mock(SendEmailResponse.class));

        this.service.sendSubscriptionConfirmationEmail(email, vehicleDetails, motExpiryDate, unsubscribeLink, motIdentificationStub());

        verify(CLIENT, times(1)).sendEmail(templateId, email, personalisationMap, "");
    }

    @Test(expected = RuntimeException.class)
    public void whenNotifyFailsExceptionIsThrown() throws NotificationClientException {

        when(CLIENT.sendEmail(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        this.service.sendSubscriptionConfirmationEmail(email, vehicleDetails, motExpiryDate, unsubscribeLink, motIdentificationStub());
    }

    @Test
    public void sendPhoneNumberConfirmationSmsIsSentWithCorrectDetails() throws NotificationClientException {

        this.service.sendPhoneNumberConfirmationSms(PHONE_NUMBER, CONFIRMATION_CODE);

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("confirmation_code", CONFIRMATION_CODE);

        verify(CLIENT, times(1)).sendSms(
                smsConfirmationTemplateId,
                PHONE_NUMBER,
                personalisation,
                ""
        );
    }

    @Test
    public void sendSubscriptionConfirmationSmsIsSentWithCorrectDetails() throws NotificationClientException {

        this.service.sendSubscriptionConfirmationSms(PHONE_NUMBER, VRM, motExpiryDate);

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", VRM);
        personalisation.put("mot_expiry_date", DateFormatter.asDisplayDate(motExpiryDate));

        verify(CLIENT, times(1)).sendSms(
                smsSubscriptionConfirmationTemplateId,
                PHONE_NUMBER,
                personalisation,
                ""
        );
    }

    private Map<String, String> stubPersonalisationMap(String vehicleDetails, LocalDate expiryDate, String link) {
        Map<String, String> map = new HashMap<>();
        map.put("vehicle_details", vehicleDetails);
        map.put("mot_expiry_date", DateFormatter.asDisplayDate(expiryDate));
        map.put("unsubscribe_link", link);
        map.put("is_due_or_expires", "expires");
        return map;
    }

    private MotIdentification motIdentificationStub() {

        return new MotIdentification(MOT_TEST_NUMBER, null);
    }
}
