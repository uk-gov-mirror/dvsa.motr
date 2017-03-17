package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidActivationIdException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.PendingSubscriptionActivatorService;
import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.EmailConfirmedViewModel;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailConfirmedResourceTest {

    private static final String SUBSCRIPTION_ID = "asdadasd";
    private static final String EMAIL = "email";
    private static final String VRM = "vrm";
    public static final LocalDate DATE = LocalDate.now();
    private TemplateEngineStub engine = new TemplateEngineStub();
    private EmailConfirmedResource resource;
    private PendingSubscriptionActivatorService pendingSubscriptionActivatorService = mock(PendingSubscriptionActivatorService.class);

    @Before
    public void setup() throws SubscriptionAlreadyExistsException, InvalidActivationIdException {

        resource = new EmailConfirmedResource(engine, pendingSubscriptionActivatorService);

        Subscription subscription = new Subscription(SUBSCRIPTION_ID).setEmail(EMAIL).setVrm(VRM).setMotDueDate(DATE);
        when(pendingSubscriptionActivatorService.activateSubscription(SUBSCRIPTION_ID)).thenReturn(subscription);
    }

    @Test
    public void subscriptionIsCreatedWhenUserConfirmsEmail() throws Exception {
        resource.confirmEmailGet(SUBSCRIPTION_ID);

        verify(pendingSubscriptionActivatorService, times(1)).activateSubscription(SUBSCRIPTION_ID);
        verifyViewModel(getViewModel());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionDoesntExist() throws Exception {
        when(pendingSubscriptionActivatorService.activateSubscription(SUBSCRIPTION_ID)).thenThrow(InvalidActivationIdException.class);

        resource.confirmEmailGet(SUBSCRIPTION_ID);

        assertEquals("subscription-error", engine.getTemplate());
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
