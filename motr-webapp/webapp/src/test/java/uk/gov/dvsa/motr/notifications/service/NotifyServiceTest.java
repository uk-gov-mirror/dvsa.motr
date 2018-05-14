package uk.gov.dvsa.motr.notifications.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.formatting.DateFormatter;
import uk.gov.dvsa.motr.web.formatting.DateFormatterForSmsDisplay;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyServiceTest {

    private NotifyService service;

    private static final NotificationClient NOTIFICATION_CLIENT_MOCK = mock(NotificationClient.class);
    private static final VehicleDetailsClient VEHICLE_DETAILS_CLIENT_MOCK = mock(VehicleDetailsClient.class);
    private static final NotifyTemplateEngine NOTIFY_TEMPLATE_ENGINE_MOCK = mock(NotifyTemplateEngine.class);

    private static final UrlHelper URL_HELPER_MOCK = mock(UrlHelper.class);

    private static final String MOT_TEST_NUMBER = "12345";
    private static final String PHONE_NUMBER = "07912345678";
    private static final String CONFIRMATION_CODE = "123456";
    private static final String VRM = "ABC123";
    private static final String BODY_CONTENTS = "This is the body";

    private String subscriptionId = "12345";
    private String email = "test@test.com";
    private String templateId = "180";
    private String emailConfirmationTemplateId = "180";
    private String smsSubscriptionConfirmationTemplateId = "SMS-SUBSCRIPTION-CONFIRMATION-TEMPLATE";
    private String smsConfirmationTemplateId = "SMS-CONFIRMATION-TEMPLATE";
    private String vehicleDetails = "TEST-MAKE TEST-MODEL, ABC123";
    private LocalDate motExpiryDate = LocalDate.of(2017, 1, 1);
    private String unsubscribeLink = "https://gov.uk/12345";

    private Map body = new HashMap<>();

    @Before
    public void setUp() {

        this.service = new NotifyService(NOTIFICATION_CLIENT_MOCK,
                templateId,
                emailConfirmationTemplateId,
                smsSubscriptionConfirmationTemplateId,
                smsConfirmationTemplateId,
                URL_HELPER_MOCK,
                VEHICLE_DETAILS_CLIENT_MOCK,
                NOTIFY_TEMPLATE_ENGINE_MOCK
                );
        body.put("body", BODY_CONTENTS);

        try {
            when(NOTIFY_TEMPLATE_ENGINE_MOCK.getNotifyParameters(any(), any())).thenReturn(body);
            when(NOTIFY_TEMPLATE_ENGINE_MOCK.getNotifyParameters(any(), any(), any())).thenReturn(body);

        } catch (NotifyTemplateEngineException exception) {
            throw new RuntimeException(exception);
        }
    }


    @Test
    public void notifyCalledWithCorrectValues()
            throws NotificationClientException, VehicleDetailsClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubPersonalisationMap(vehicleDetails, motExpiryDate, unsubscribeLink);

        when(NOTIFICATION_CLIENT_MOCK.sendEmail(any(), any(), any(), any())).thenReturn(mock(SendEmailResponse.class));
        when(VEHICLE_DETAILS_CLIENT_MOCK.fetchByVrm(VRM)).thenReturn(mockVehicleDetailsStub());
        when(URL_HELPER_MOCK.unsubscribeLink(subscriptionId)).thenReturn(unsubscribeLink);

        this.service.sendSubscriptionConfirmation(emailSubscriptionStub());
        verify(NOTIFY_TEMPLATE_ENGINE_MOCK, times(1)).getNotifyParameters(any(), any(), Matchers.eq(personalisation));
        verify(NOTIFICATION_CLIENT_MOCK, times(1)).sendEmail(templateId, email, body, "");
    }

    @Test(expected = RuntimeException.class)
    public void whenNotifyFailsExceptionIsThrown() throws NotificationClientException {

        when(NOTIFICATION_CLIENT_MOCK.sendEmail(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        this.service.sendSubscriptionConfirmation(emailSubscriptionStub());
    }

    @Test
    public void sendPhoneNumberConfirmationSmsIsSentWithCorrectDetails() throws NotificationClientException, NotifyTemplateEngineException {


        this.service.sendPhoneNumberConfirmationSms(PHONE_NUMBER, CONFIRMATION_CODE);

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("confirmation_code", CONFIRMATION_CODE);

        verify(NOTIFY_TEMPLATE_ENGINE_MOCK, times(1)).getNotifyParameters(any(), Matchers.eq(personalisation));

        verify(NOTIFICATION_CLIENT_MOCK, times(1)).sendSms(
                smsConfirmationTemplateId,
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void sendSubscriptionConfirmationSmsIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException {

        this.service.sendSubscriptionConfirmation(smsSubscriptionStub());

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", VRM);
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(motExpiryDate));

        verify(NOTIFY_TEMPLATE_ENGINE_MOCK, times(1)).getNotifyParameters(any(), Matchers.eq(personalisation));

        verify(NOTIFICATION_CLIENT_MOCK, times(1)).sendSms(
                smsSubscriptionConfirmationTemplateId,
                PHONE_NUMBER,
                body,
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

    private Subscription emailSubscriptionStub() {

        return new Subscription()
                .setUnsubscribeId(subscriptionId)
                .setMotDueDate(motExpiryDate)
                .setContactDetail(new ContactDetail(email, Subscription.ContactType.EMAIL))
                .setVrm(VRM)
                .setMotIdentification(motIdentificationStub());
    }

    private Subscription smsSubscriptionStub() {

        return new Subscription()
                .setUnsubscribeId(subscriptionId)
                .setMotDueDate(motExpiryDate)
                .setContactDetail(new ContactDetail(PHONE_NUMBER, Subscription.ContactType.MOBILE))
                .setVrm(VRM)
                .setMotIdentification(motIdentificationStub());
    }

    private Optional<VehicleDetails> mockVehicleDetailsStub() {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("test-Make");
        vehicleDetails.setModel("TEST-Model");

        return Optional.of(vehicleDetails);
    }
}
