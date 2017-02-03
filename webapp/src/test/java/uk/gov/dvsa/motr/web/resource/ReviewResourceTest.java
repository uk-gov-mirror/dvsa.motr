package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.util.Map;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class ReviewResourceTest {

    @Test
    public void reviewTemplateIsRenderedOnGetWithViewModel() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        ReviewResource resource = new ReviewResource(engine);

        assertEquals(RESPONSE, resource.reviewPage());
        assertEquals("review", engine.getTemplate());
        assertEquals(ReviewViewModel.class, engine.getContext(Map.class).get("viewModel").getClass());
    }
}
