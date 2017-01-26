package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.validator.EmailValidator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class EmailResourceTest {

    @Test
    public void emailTemplateIsRenderedOnGet() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        EmailResource resource = new EmailResource(engine);

        assertEquals(RESPONSE, resource.emailPage());
        assertEquals("email", engine.getTemplate());
        assertEquals(new HashMap<>(), engine.getContext(Map.class));
    }

    @Test
    public void onPostWithValidEmailPageReloaded() throws Exception {

        // Temporary test until DB integration and redirect is in place
        TemplateEngineStub engine = new TemplateEngineStub();
        EmailResource resource = new EmailResource(engine);

        assertEquals(RESPONSE, resource.emailPagePost("test@test.com"));
        assertEquals("email", engine.getTemplate());
        assertEquals(new HashMap<>(), engine.getContext(Map.class));
    }

    @Test
    public void onPostWithInvalidEmailFormatMessageWillBePassedToView() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        EmailResource resource = new EmailResource(engine);

        HashMap<String, String> expectedContext = new HashMap<>();
        expectedContext.put("message", EmailValidator.EMAIL_INVALID_MESSAGE);

        assertEquals(RESPONSE, resource.emailPagePost("invalidEmail"));
        assertEquals("email", engine.getTemplate());
        assertEquals(expectedContext, engine.getContext(Map.class));
    }

    @Test
    public void onPostWithEmptyEmailFormatMessageWillBePassedToView() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        EmailResource resource = new EmailResource(engine);

        HashMap<String, String> expectedContext = new HashMap<>();
        expectedContext.put("message", EmailValidator.EMAIL_EMPTY_MESSAGE);

        assertEquals(RESPONSE, resource.emailPagePost(""));
        assertEquals("email", engine.getTemplate());
        assertEquals(expectedContext, engine.getContext(Map.class));
    }
}
