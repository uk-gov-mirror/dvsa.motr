package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyConfirmedException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.SubscriptionConfirmationParams;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionConfirmedResourceTest {

    private static final String CONFIRMATION_ID = "asdadasd";
    private static final String UNSUBSCRIBE_ID = "unsubscribe_id";
    private static final String EMAIL = "email";
    private static final String VRM = "vrm";
    private static final String CONTACT_TYPE_EMAIL = "EMAIL";
    private static final String CONTACT_TYPE_MOBILE = "MOBILE";
    private static final String TEST_NUMBER = "12345";
    private static final String DVLA_ID = "54321";
    private static final String MOT_TEST_NUMBER = "12345";
    private static final String REPLY_PHONE_NUMBER = "07491163045";

    public static final LocalDate DATE = LocalDate.now();
    private TemplateEngineStub engine = new TemplateEngineStub();
    private SubscriptionConfirmedResource resource;
    private SubscriptionConfirmationService pendingSubscriptionActivatorService = mock(SubscriptionConfirmationService.class);
    private UrlHelper urlHelper = mock(UrlHelper.class);
    private MotrSession motrSession = mock(MotrSession.class);

    @Before
    public void setup() throws SubscriptionAlreadyExistsException, InvalidConfirmationIdException {

        resource = new SubscriptionConfirmedResource(
                engine,
                pendingSubscriptionActivatorService,
                motrSession,
                urlHelper
        );

        when(urlHelper.subscriptionConfirmedFirstTimeLink()).thenReturn("confirm-subscription/confirmed");
        when(urlHelper.subscriptionConfirmedNthTimeLink(any(Subscription.ContactType.class)))
                .thenReturn("confirm-subscription/already-confirmed");
    }

    @Test
    public void subscriptionIsCreatedWhenUserConfirmsEmail() throws Exception {

        mockSubscription(TEST_NUMBER, null);
        ArgumentCaptor<SubscriptionConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(SubscriptionConfirmationParams.class);

        Response response = resource.confirmSubscriptionGet(CONFIRMATION_ID);

        verify(pendingSubscriptionActivatorService, times(1)).confirmSubscription(CONFIRMATION_ID);
        verify(motrSession, times(1)).setSubscriptionConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals("confirm-subscription/confirmed", response.getLocation().toString());
        assertEquals(VRM, paramsArgumentCaptor.getValue().getRegistration());
    }

    @Test
    public void sessionIsClearedPendingSubscriptionConfirmed() throws Exception {

        mockSubscription(TEST_NUMBER, null);
        resource.confirmSubscriptionGet(CONFIRMATION_ID);

        verify(motrSession).clear();
    }

    @Test
    public void dataLayerIsBeingStoredOnFirstVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmSubscriptionFirstTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerStoresDvlaIdWhenMotTestNumberNotPresent() {

        motrSessionWillReturnValidPageParams();

        resource.confirmSubscriptionFirstTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerIsBeingStoredOnNthVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmSubscriptionNthTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerIsPopulatedWithDvlaIdWhenMotTestNumberNotPresent() throws Exception {

        SubscriptionConfirmationParams confirmationParams = createSubscriptionConfirmationParams(CONTACT_TYPE_EMAIL);
        confirmationParams.setMotTestNumber(null);
        confirmationParams.setDvlaId(DVLA_ID);
        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);

        resource.confirmSubscriptionFirstTimeGet();

        assertEquals("{\"vrm\":\"vrm\",\"dvla-id\":\"54321\",\"contact-type\":\"EMAIL\"}", getDataLayer());
    }

    @Test
    public void dataLayerIsPopulatedWithMotTestNumberWhenMotTestNumberIsPresent() throws Exception {

        SubscriptionConfirmationParams confirmationParams = createSubscriptionConfirmationParams(CONTACT_TYPE_EMAIL);
        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);

        resource.confirmSubscriptionFirstTimeGet();

        assertEquals("{\"vrm\":\"vrm\",\"mot-test-number\":\"12345\",\"contact-type\":\"EMAIL\"}", getDataLayer());
    }

    @Test
    public void modelIsPopulatedWithSmsRelatedDataWhenConfirmedSignUpSms() throws Exception {

        SubscriptionConfirmationParams confirmationParams = createSubscriptionConfirmationParams(CONTACT_TYPE_MOBILE);
        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("usingSms", true);
        expectedMap.put("replyNumber", REPLY_PHONE_NUMBER);
        expectedMap.put("registration", VRM);

        resource.confirmSubscriptionFirstTimeGet();

        assertEquals("{\"vrm\":\"vrm\",\"mot-test-number\":\"12345\",\"contact-type\":\"MOBILE\"}", getDataLayer());
        assertEquals(expectedMap.get("usingSms"), engine.getContext(Map.class).get("usingSms"));
        assertEquals(expectedMap.get("replyNumber"), engine.getContext(Map.class).get("replyNumber"));
        assertEquals(expectedMap.get("registration"), engine.getContext(Map.class).get("registration"));
    }

    @Test
    public void isMotVehicleIsSetToFalseInModelMapWhenSubscriptionIsForHgvVehicle() throws Exception {

        SubscriptionConfirmationParams confirmationParams = createSubscriptionConfirmationParams(CONTACT_TYPE_MOBILE);
        confirmationParams.setVehicleType(VehicleType.HGV);

        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);

        resource.confirmSubscriptionFirstTimeGet();

        assertFalse((boolean) engine.getContext(Map.class).get("isMotVehicle"));
    }

    @Test
    public void isMotVehicleIsSetToTrueInModelMapWhenSubscriptionIsForMotVehicle() throws Exception {

        SubscriptionConfirmationParams confirmationParams = createSubscriptionConfirmationParams(CONTACT_TYPE_MOBILE);
        confirmationParams.setVehicleType(VehicleType.MOT);

        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);

        resource.confirmSubscriptionFirstTimeGet();

        assertTrue((boolean) engine.getContext(Map.class).get("isMotVehicle"));
    }

    @Test
    public void userIsRedirectedWhenSubscriptionIsForMotVehicleAndSubscriptionAlreadyConfirmed() throws Exception {
        MotIdentification motIdentification = new MotIdentification(MOT_TEST_NUMBER, DVLA_ID);
        Subscription subscription = new Subscription();
        subscription.setVrm(VRM);
        subscription.setVehicleType(VehicleType.MOT);
        subscription.setUnsubscribeId(UNSUBSCRIBE_ID);
        subscription.setContactDetail(new ContactDetail(EMAIL, Subscription.ContactType.EMAIL));
        subscription.setMotDueDate(DATE);
        subscription.setMotIdentification(motIdentification);
        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID))
                .thenThrow(new SubscriptionAlreadyConfirmedException(subscription));

        ArgumentCaptor<SubscriptionConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(SubscriptionConfirmationParams.class);

        Response response = resource.confirmSubscriptionGet(CONFIRMATION_ID);

        verify(pendingSubscriptionActivatorService, times(1)).confirmSubscription(CONFIRMATION_ID);
        verify(motrSession, times(1)).setSubscriptionConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals("confirm-subscription/already-confirmed", response.getLocation().toString());
        assertEquals(VehicleType.MOT, paramsArgumentCaptor.getValue().getVehicleType());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionDoesntExist() throws Exception {

        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenThrow(InvalidConfirmationIdException.class);

        resource.confirmSubscriptionGet(CONFIRMATION_ID);

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnFirstVisit() throws Exception {

        resource.confirmSubscriptionFirstTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnNthVisit() throws Exception {

        resource.confirmSubscriptionNthTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    private void motrSessionWillReturnValidPageParams() {

        SubscriptionConfirmationParams confirmationParams = createSubscriptionConfirmationParams(CONTACT_TYPE_EMAIL);
        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);
        when(motrSession.isAllowedOnChannelSelectionPage()).thenReturn(false);
        when(motrSession.isUsingSmsChannel()).thenReturn(false);
    }

    private SubscriptionConfirmationParams createSubscriptionConfirmationParams(String contactType) {

        SubscriptionConfirmationParams confirmationParams = new SubscriptionConfirmationParams();

        confirmationParams.setRegistration(VRM);
        confirmationParams.setMotTestNumber(MOT_TEST_NUMBER);
        confirmationParams.setContactType(contactType);

        return confirmationParams;
    }

    private String getDataLayer() {

        return engine.getContext(Map.class).get("dataLayer").toString();
    }

    private void verifyDataLayer(String dataLayer) {

        assertEquals("{\"vrm\":\"vrm\",\"mot-test-number\":\"12345\",\"contact-type\":\"EMAIL\"}", dataLayer);
    }

    private void mockSubscription(String motTestNumber, String dvlaId) throws SubscriptionAlreadyConfirmedException,
            InvalidConfirmationIdException {

        MotIdentification motIdentification = new MotIdentification(motTestNumber, dvlaId);
        Subscription subscription = new Subscription()
                .setUnsubscribeId(UNSUBSCRIBE_ID)
                .setContactDetail(new ContactDetail(EMAIL, Subscription.ContactType.EMAIL))
                .setVrm(VRM)
                .setMotDueDate(DATE)
                .setMotIdentification(motIdentification);
        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenReturn(subscription);
    }
}
