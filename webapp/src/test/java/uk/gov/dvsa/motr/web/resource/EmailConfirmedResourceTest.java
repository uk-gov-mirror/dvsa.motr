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
import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.EmailConfirmedViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailConfirmedResourceTest {

    private static final String UNSUBSCRIBE_ID = "asdadasd";
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
        when(pendingSubscriptionActivatorService.confirmSubscription(UNSUBSCRIBE_ID)).thenReturn(subscription);
        when(urlHelper.emailConfirmedFirstTimeLink()).thenReturn("confirm-email/confirmed");
    }

    @Test
    public void subscriptionIsCreatedWhenUserConfirmsEmail() throws Exception {

        ArgumentCaptor<EmailConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(EmailConfirmationParams.class);

        Response response = resource.confirmEmailGet(UNSUBSCRIBE_ID);

        verify(pendingSubscriptionActivatorService, times(1)).confirmSubscription(UNSUBSCRIBE_ID);
        verify(motrSession, times(1)).setEmailConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals("confirm-email/confirmed", response.getLocation().toString());
        assertEquals(VRM, paramsArgumentCaptor.getValue().getRegistration());
        assertEquals(EMAIL, paramsArgumentCaptor.getValue().getEmail());
        assertEquals(DATE.format(DateTimeFormatter.ofPattern("d MMMM u")), paramsArgumentCaptor.getValue().getExpiryDate());
    }

    @Test
    public void subscriptionDetailsAreBeignShownIfPresentInTheSessionOnFirstVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmEmailFirstTimeGet();

        verifyViewModel(getViewModel());
    }

    @Test
    public void subscriptionDetailsAreBeignShownIfPresentInTheSessionOnNthVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmEmailNthTimeGet();

        verifyViewModel(getViewModel());
    }


    @Test
    public void errorPageIsShownWhenSubscriptionDoesntExist() throws Exception {

        when(pendingSubscriptionActivatorService.confirmSubscription(UNSUBSCRIBE_ID)).thenThrow(InvalidConfirmationIdException.class);

        resource.confirmEmailGet(UNSUBSCRIBE_ID);

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
        confirmationParams.setEmail(EMAIL);
        confirmationParams.setRegistration(VRM);
        confirmationParams.setExpiryDate(DATE.format(DateTimeFormatter.ofPattern("d MMMM u")));
        when(motrSession.getEmailConfirmationParams()).thenReturn(confirmationParams);
    }

    private EmailConfirmedViewModel getViewModel() {

        return (EmailConfirmedViewModel) engine.getContext(Map.class).get("viewModel");
    }

    private void verifyViewModel(EmailConfirmedViewModel viewModel) {

        assertEquals(VRM, viewModel.getRegistration());
        assertEquals(EMAIL, viewModel.getEmail());
        assertEquals(DateDisplayHelper.asDisplayDate(DATE), viewModel.getExpiryDate());
    }
}
