package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Optional.empty;

public class UnsubscribeResourceTest {

    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final String UNSUBSCRIBE_ID = "123-test-id";

    private UnsubscribeResource resource;
    private UnsubscribeService unsubscribeService;

    @Before
    public void setUp() {

        MotrSession motrSession = new MotrSession();
        UnsubscribeConfirmationParams params = new UnsubscribeConfirmationParams();

        params.setExpiryDate(LocalDate.of(2015, 7, 10).toString());
        params.setRegistration("TEST-VRM");
        params.setEmail("test@this-is-a-test-123");
        motrSession.setUnsubscribeConfirmationParams(params);

        this.unsubscribeService = mock(UnsubscribeService.class);
        this.resource = new UnsubscribeResource(unsubscribeService, TEMPLATE_ENGINE_STUB, motrSession);
    }

    @Test(expected = NotFoundException.class)
    public void unsubscribeGetNotFoundExceptionThrownIfVehicleIsNotFound() throws Exception {

        when(unsubscribeService.findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID)).thenReturn(empty());

        resource.unsubscribeGet(UNSUBSCRIBE_ID);

        verify(unsubscribeService, times(1)).findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID);
    }

    @Test
    public void unsubscribeGetWhenFoundDisplayPage() throws Exception {

        when(unsubscribeService.findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID)).thenReturn(Optional.of(subscriptionStub()));

        resource.unsubscribeGet(UNSUBSCRIBE_ID);

        verify(unsubscribeService, times(1)).findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID);
        assertEquals(UnsubscribeViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
        UnsubscribeViewModel viewModel = (UnsubscribeViewModel) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel");
        assertEquals("test@this-is-a-test-123", viewModel.getEmail());
        assertEquals("10 July 2015", viewModel.getExpiryDate());
        assertEquals("TEST-VRM", viewModel.getRegistration());
    }

    @Test
    public void unsubscribePostRedirectsToUnSubscribeConfirmationOnSuccess() throws Exception {

        Subscription subscription = subscriptionStub();
        when(unsubscribeService.unsubscribe(UNSUBSCRIBE_ID)).thenReturn(subscription);

        Response actual = resource.unsubscribePost(UNSUBSCRIBE_ID);

        verify(unsubscribeService, times(1)).unsubscribe(UNSUBSCRIBE_ID);
        assertEquals(302, actual.getStatus());
        assertEquals(
                "unsubscribe/confirmed",
                actual.getHeaderString("Location")
        );
    }

    @Test(expected = NotFoundException.class)
    public void unsubscribeConfirmedWillThrow404WhenSessionIsEmpty() throws Exception {

        resource = new UnsubscribeResource(unsubscribeService, TEMPLATE_ENGINE_STUB, new MotrSession());
        resource.unsubscribeConfirmed();
    }

    @Test
    public void unsubscribeConfirmedDisplaysPage() throws Exception {

        resource.unsubscribeConfirmed();

        assertEquals(UnsubscribeViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
        String dataLayerString = (String) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("dataLayer");
        UnsubscribeViewModel viewModel = (UnsubscribeViewModel) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel");
        assertEquals("test@this-is-a-test-123", viewModel.getEmail());
        assertEquals("10 July 2015", viewModel.getExpiryDate());
        assertEquals("TEST-VRM", viewModel.getRegistration());
        assertEquals("{\"vrm\":\"TEST-VRM\"}", dataLayerString);
    }


    private Subscription subscriptionStub() {
        return new Subscription()
                .setUnsubscribeId(UNSUBSCRIBE_ID)
                .setEmail("test@this-is-a-test-123")
                .setMotDueDate(LocalDate.of(2015, 7, 10))
                .setVrm("TEST-VRM");
    }
}
