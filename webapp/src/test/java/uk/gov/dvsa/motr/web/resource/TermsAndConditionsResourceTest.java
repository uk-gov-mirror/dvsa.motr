package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class TermsAndConditionsResourceTest {
    @Test
    public void termsTemplateIsRenderedWhenTermsPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        TermsAndConditionsResource resource = new TermsAndConditionsResource(engine);

        assertEquals(RESPONSE, resource.termsPage());
        assertEquals("terms-and-conditions", engine.getTemplate());
    }
}
