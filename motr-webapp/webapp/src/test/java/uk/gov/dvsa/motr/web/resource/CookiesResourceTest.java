package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class CookiesResourceTest {

    @Test
    public void cookiesTemplateIsRenderedWhenCookiesPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        CookiesResource resource = new CookiesResource(engine);

        assertEquals(RESPONSE, resource.cookiesPage());
        assertEquals("cookies", engine.getTemplate());
    }
}
