package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class EmailConfirmationPendingResourceTest {

    @Test
    public void termsTemplateIsRenderedWhenEmailConfirmPendingPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        MotrSession motrSession = mock(MotrSession.class);
        EmailConfirmationPendingResource resource = new EmailConfirmationPendingResource(engine, motrSession);

        assertEquals(RESPONSE, resource.confirmEmailGet());
        assertEquals("email-confirmation-pending", engine.getTemplate());
        verify(motrSession, times(1)).setShouldClearCookies(true);
    }
}
