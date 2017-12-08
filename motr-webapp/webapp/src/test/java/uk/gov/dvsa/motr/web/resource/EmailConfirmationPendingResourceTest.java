package uk.gov.dvsa.motr.web.resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.EmailConfirmationPendingViewModel;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class EmailConfirmationPendingResourceTest {

    private MotrSession motrSession;
    private TemplateEngineStub engine;
    private EmailConfirmationPendingResource resource;

    @Before
    public void setup() {

        motrSession = mock(MotrSession.class);
        engine = new TemplateEngineStub();
        resource = new EmailConfirmationPendingResource(engine, motrSession);
    }

    @After
    public void clearDown() {

        reset(motrSession);
    }

    @Test
    public void termsTemplateIsRenderedWhenEmailConfirmPendingPathAccessed_correctDisplayWhenInSession() throws Exception {

        when(motrSession.getEmailFromSession()).thenReturn("test@test.com");

        assertEquals(RESPONSE, resource.confirmEmailGet());
        assertEquals("email-confirmation-pending", engine.getTemplate());
        verify(motrSession, times(1)).setShouldClearCookies(true);

        EmailConfirmationPendingViewModel viewModel = (EmailConfirmationPendingViewModel) engine.getContext(Map.class).get("viewModel");
        assertEquals("display email is not the same", viewModel.getEmailDisplayString(), "test@test.com ");
        assertSame(viewModel.getClass(), EmailConfirmationPendingViewModel.class);
    }

    @Test
    public void termsTemplateIsRenderedWhenEmailConfirmPendingPathAccessed_emptyStringWhenNotInSession() throws Exception {

        when(motrSession.getEmailFromSession()).thenReturn(null);

        assertEquals(RESPONSE, resource.confirmEmailGet());
        assertEquals("email-confirmation-pending", engine.getTemplate());
        verify(motrSession, times(1)).setShouldClearCookies(true);

        EmailConfirmationPendingViewModel viewModel = (EmailConfirmationPendingViewModel) engine.getContext(Map.class).get("viewModel");
        assertEquals("display email is not the same", viewModel.getEmailDisplayString(), "");
        assertSame(viewModel.getClass(), EmailConfirmationPendingViewModel.class);
    }
}
