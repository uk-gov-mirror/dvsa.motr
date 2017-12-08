package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class StartResourceTest {

    private MotrSession motrSession;

    @Test
    public void startClearsCookiesAndRedirectsToVrmPage() throws Exception {

        motrSession = mock(MotrSession.class);
        StartResource resource = new StartResource(motrSession);
        Response response = resource.start();
        verify(motrSession, times(1)).setShouldClearCookies(true);
        assertEquals(Response.Status.FOUND.getStatusCode(), response.getStatus());
        assertEquals("/vrm", response.getHeaders().get("Location").get(0).toString());
    }
}
