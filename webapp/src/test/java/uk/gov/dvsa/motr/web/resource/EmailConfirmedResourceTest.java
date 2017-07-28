package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.cookie.EmailConfirmationParams;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.time.LocalDate;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailConfirmedResourceTest {

    private static final String CONFIRMATION_ID = "asdadasd";
    private static final String UNSUBSCRIBE_ID = "unsubscribe_id";
    private static final String EMAIL = "email";
    private static final String VRM = "vrm";

    public static final LocalDate DATE = LocalDate.now();
    private TemplateEngineStub engine = new TemplateEngineStub();
    private EmailConfirmedResource resource;
    private SubscriptionConfirmationService pendingSubscriptionActivatorService = mock(SubscriptionConfirmationService.class);
    private UrlHelper urlHelper = mock(UrlHelper.class);
    private MotrSession motrSession = mock(MotrSession.class);

    @Before
    public void setup() throws SubscriptionAlreadyExistsException, InvalidConfirmationIdException {

        resource = new EmailConfirmedResource(
                engine,
                pendingSubscriptionActivatorService,
                motrSession,
                urlHelper
        );

        Subscription subscription = new Subscription().setUnsubscribeId(UNSUBSCRIBE_ID).setEmail(EMAIL).setVrm(VRM).setMotDueDate(DATE);
        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenReturn(subscription);
        when(urlHelper.emailConfirmedFirstTimeLink()).thenReturn("confirm-email/confirmed");
    }

    @Test
    public void subscriptionIsCreatedWhenUserConfirmsEmail() throws Exception {

        ArgumentCaptor<EmailConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(EmailConfirmationParams.class);

        Response response = resource.confirmEmailGet(CONFIRMATION_ID);

        verify(pendingSubscriptionActivatorService, times(1)).confirmSubscription(CONFIRMATION_ID);
        verify(motrSession, times(1)).setEmailConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals("confirm-email/confirmed", response.getLocation().toString());
        assertEquals(VRM, paramsArgumentCaptor.getValue().getRegistration());
    }

    @Test
    public void sessionIsClearedPendingSubscriptionConfirmed() throws Exception {

        resource.confirmEmailGet(CONFIRMATION_ID);

        verify(motrSession).clear();
    }

    @Test
    public void dataLayerIsBeingStoredOnFirstVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmEmailFirstTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerIsBeingStoredOnNthVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmEmailNthTimeGet();
        verifyDataLayer(getDataLayer());
    }


    @Test
    public void errorPageIsShownWhenSubscriptionDoesntExist() throws Exception {

        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenThrow(InvalidConfirmationIdException.class);

        resource.confirmEmailGet(CONFIRMATION_ID);

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnFirstVisit() throws Exception {

        resource.confirmEmailFirstTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnNthVisit() throws Exception {

        resource.confirmEmailNthTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    private void motrSessionWillReturnValidPageParams() {

        EmailConfirmationParams confirmationParams = new EmailConfirmationParams();
        confirmationParams.setRegistration(VRM);
        when(motrSession.getEmailConfirmationParams()).thenReturn(confirmationParams);
    }

    private String getDataLayer() {

        return engine.getContext(Map.class).get("dataLayer").toString();
    }

    private void verifyDataLayer(String dataLayer) {

        assertEquals("{\"vrm\":\"vrm\"}", dataLayer);
    }
}
