package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class EmailConfirmationPendingResourceTest {

    @Test
    public void termsTemplateIsRenderedWhenEmailConfirmPendingPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        EmailConfirmationPendingResource resource = new EmailConfirmationPendingResource(engine);

        assertEquals(RESPONSE, resource.confirmEmailGet());
        assertEquals("email-confirmation-pending", engine.getTemplate());
    }
}
