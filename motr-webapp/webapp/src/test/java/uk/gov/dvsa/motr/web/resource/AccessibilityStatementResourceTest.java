package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class AccessibilityStatementResourceTest {
    @Test
    public void accessibilityTemplateIsRenderedWhenTermsPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        AccessibilityStatementResource resource = new AccessibilityStatementResource(engine);

        assertEquals(RESPONSE, resource.termsPage());
        assertEquals("accessibility-statement", engine.getTemplate());
    }
}