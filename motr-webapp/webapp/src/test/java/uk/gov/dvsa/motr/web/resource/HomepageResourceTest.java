package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import static org.junit.Assert.assertEquals;


public class HomepageResourceTest {

    @Test
    public void homepageRedirectsToGovWebsite() throws Exception {

        HomepageResource resource = new HomepageResource();
        Response response = resource.homePage();
        assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
        assertEquals(HomepageResource.HOMEPAGE_URL, response.getHeaders().get("Location").get(0).toString());
    }
}
