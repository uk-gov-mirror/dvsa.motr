package uk.gov.dvsa.motr.notifications.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyServiceTest {

    private NotifyService service;

    private NotificationClient notificationClientMock;
    private VehicleDetailsClient vehicleDetailsClientMock;
    private NotifyTemplateEngine notifyTemplateEngineMock;

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

    private static final String HGV_PSV_TEMPLATES_DIRECTORY = "hgv-psv/";
    private static final String SIGNED_UP_COMPLETE_EMAIL_SUBJECT = "signed-up-complete-email-subject.txt";
    private static final String SIGNED_UP_COMPLETE_EMAIL_BODY = "signed-up-complete-email-body.txt";
    private static final String SIGNED_UP_COMPLETE_SMS = "signed-up-complete-sms.txt";

    private String smsConfirmationTemplateId = "SMS-CONFIRMATION-TEMPLATE";
    private String vehicleDetails = "TEST-MAKE TEST-MODEL, ABC123";
    private LocalDate motExpiryDate = LocalDate.of(2017, 1, 1);
    private String unsubscribeLink = "https://gov.uk/12345";

    private Map body = new HashMap<>();

    @Before
    public void setUp() {

        notificationClientMock = mock(NotificationClient.class);
        vehicleDetailsClientMock = mock(VehicleDetailsClient.class);
        notifyTemplateEngineMock = mock(NotifyTemplateEngine.class);

        this.service = new NotifyService(notificationClientMock,
                templateId,
                emailConfirmationTemplateId,
                smsSubscriptionConfirmationTemplateId,
                smsConfirmationTemplateId,
                URL_HELPER_MOCK,
                vehicleDetailsClientMock,
                notifyTemplateEngineMock
                );
        body.put("body", BODY_CONTENTS);

        try {
            when(notifyTemplateEngineMock.getNotifyParameters(any(), any())).thenReturn(body);
            when(notifyTemplateEngineMock.getNotifyParameters(any(), any(), any())).thenReturn(body);

        } catch (NotifyTemplateEngineException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void notifyCalledWithCorrectValues()
            throws NotificationClientException, VehicleDetailsClientException, NotifyTemplateEngineException {

        Map<String, String> personalisation = stubPersonalisationMap(vehicleDetails, unsubscribeLink);

        when(notificationClientMock.sendEmail(any(), any(), any(), any())).thenReturn(mock(SendEmailResponse.class));
        when(vehicleDetailsClientMock.fetchByVrm(VRM)).thenReturn(mockVehicleDetailsStub());
        when(URL_HELPER_MOCK.unsubscribeLink(subscriptionId)).thenReturn(unsubscribeLink);

        this.service.sendSubscriptionConfirmation(emailSubscriptionStub());
        verify(notifyTemplateEngineMock, times(1)).getNotifyParameters(any(), any(), eq(personalisation));
        verify(notificationClientMock, times(1)).sendEmail(templateId, email, body, "");
    }

    @Test(expected = RuntimeException.class)
    public void whenNotifyFailsExceptionIsThrown() throws NotificationClientException {

        when(notificationClientMock.sendEmail(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        this.service.sendSubscriptionConfirmation(emailSubscriptionStub());
    }

    @Test
    public void sendPhoneNumberConfirmationSmsIsSentWithCorrectDetails() throws NotificationClientException, NotifyTemplateEngineException {

        this.service.sendPhoneNumberConfirmationSms(PHONE_NUMBER, CONFIRMATION_CODE);

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("confirmation_code", CONFIRMATION_CODE);

        verify(notifyTemplateEngineMock, times(1)).getNotifyParameters(any(), eq(personalisation));

        verify(notificationClientMock, times(1)).sendSms(
                smsConfirmationTemplateId,
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void emailSubscriptionComplete_sentWithCorrectDetails_forHgvPsv()
            throws NotifyTemplateEngineException, VehicleDetailsClientException {

        when(vehicleDetailsClientMock.fetchByVrm(any())).thenReturn(Optional.of(
                new VehicleDetails().setVehicleType(VehicleType.PSV)
        ));

        this.service.sendSubscriptionConfirmation(emailSubscriptionStub());

        verify(notifyTemplateEngineMock, times(1)).getNotifyParameters(
                eq(HGV_PSV_TEMPLATES_DIRECTORY + SIGNED_UP_COMPLETE_EMAIL_SUBJECT),
                eq(HGV_PSV_TEMPLATES_DIRECTORY + SIGNED_UP_COMPLETE_EMAIL_BODY),
                any()
        );
    }

    @Test
    public void emailSubscriptionComplete_sentWithCorrectDetails_forMotVehicle()
            throws NotifyTemplateEngineException, VehicleDetailsClientException {

        when(vehicleDetailsClientMock.fetchByVrm(any())).thenReturn(Optional.of(
                new VehicleDetails().setVehicleType(VehicleType.MOT)
        ));

        this.service.sendSubscriptionConfirmation(emailSubscriptionStub());

        verify(notifyTemplateEngineMock, times(1)).getNotifyParameters(
                eq(SIGNED_UP_COMPLETE_EMAIL_SUBJECT),
                eq(SIGNED_UP_COMPLETE_EMAIL_BODY),
                any()
        );
    }

    @Test
    public void sendSubscriptionConfirmationSmsIsSentWithCorrectDetails()
            throws NotificationClientException, NotifyTemplateEngineException, VehicleDetailsClientException {

        when(vehicleDetailsClientMock.fetchByVrm(any())).thenReturn(Optional.of(new VehicleDetails()));

        this.service.sendSubscriptionConfirmation(smsSubscriptionStub());

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", VRM);

        verify(notifyTemplateEngineMock, times(1)).getNotifyParameters(any(), eq(personalisation));

        verify(notificationClientMock, times(1)).sendSms(
                smsSubscriptionConfirmationTemplateId,
                PHONE_NUMBER,
                body,
                ""
        );
    }

    @Test
    public void smsSubscriptionComplete_sentWithCorrectDetails_forHgvPsv()
            throws NotifyTemplateEngineException, VehicleDetailsClientException {

        when(vehicleDetailsClientMock.fetchByVrm(any())).thenReturn(Optional.of(
                new VehicleDetails().setVehicleType(VehicleType.PSV)
        ));

        this.service.sendSubscriptionConfirmation(smsSubscriptionStub());

        verify(notifyTemplateEngineMock, times(1)).getNotifyParameters(
                eq(HGV_PSV_TEMPLATES_DIRECTORY + SIGNED_UP_COMPLETE_SMS),
                any()
        );
    }

    @Test
    public void smsSubscriptionComplete_sentWithCorrectDetails_forMotVehicle()
            throws NotifyTemplateEngineException, VehicleDetailsClientException {

        when(vehicleDetailsClientMock.fetchByVrm(any())).thenReturn(Optional.of(
                new VehicleDetails().setVehicleType(VehicleType.MOT)
        ));

        this.service.sendSubscriptionConfirmation(smsSubscriptionStub());

        verify(notifyTemplateEngineMock, times(1)).getNotifyParameters(
                eq(SIGNED_UP_COMPLETE_SMS),
                any()
        );
    }

    private Map<String, String> stubPersonalisationMap(String vehicleDetails, String link) {

        Map<String, String> map = new HashMap<>();
        map.put("vehicle_details", vehicleDetails);
        map.put("unsubscribe_link", link);
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
