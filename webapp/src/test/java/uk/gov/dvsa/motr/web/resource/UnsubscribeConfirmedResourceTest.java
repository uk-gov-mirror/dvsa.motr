package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.Map;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;

public class UnsubscribeConfirmedResourceTest {

    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();

    private UnsubscribeConfirmedResource resource;

    @Before
    public void setUp() {

        MotrSession motrSession = new MotrSession();
        UnsubscribeConfirmationParams params = new UnsubscribeConfirmationParams();

        params.setExpiryDate(LocalDate.of(2015, 7, 10).toString());
        params.setRegistration("TEST-VRM");
        params.setEmail("test@this-is-a-test-123");
        motrSession.setUnsubscribeConfirmationParams(params);

        this.resource = new UnsubscribeConfirmedResource(TEMPLATE_ENGINE_STUB, motrSession);
    }

    @Test(expected = NotFoundException.class)
    public void unsubscribeConfirmedWillThrow404WhenSessionIsEmpty() throws Exception {

        resource = new UnsubscribeConfirmedResource(TEMPLATE_ENGINE_STUB, new MotrSession());
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
}
