package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.SubscriptionConfirmationViewModel;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SubscriptionConfirmationResourceTest {

    private TemplateEngineStub templateEngineStub;
    private SubscriptionConfirmationResource resource;

    @Before
    public void setUp() {

        templateEngineStub = new TemplateEngineStub();
        resource = new SubscriptionConfirmationResource(templateEngineStub);
    }

    @Test
    public void getResultsInSubscriptionConfirmationTemplate() {

        resource.subscriptionConfirmationGet();

        assertEquals("subscription-confirmation", templateEngineStub.getTemplate());
        assertEquals(SubscriptionConfirmationViewModel.class, templateEngineStub.getContext(Map.class).get("viewModel").getClass());
    }
}
