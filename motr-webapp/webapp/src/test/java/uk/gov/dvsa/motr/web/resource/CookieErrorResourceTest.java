package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class CookieErrorResourceTest {

    @Test
    public void cookieErrorTemplateIsRenderedWhenCookiesPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        CookieErrorResource resource = new CookieErrorResource(engine,  mock(MotrSession.class));

        assertEquals(RESPONSE, resource.cookieErrorPage());
        assertEquals("error/cookie-error", engine.getTemplate());
    }
}
