package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import javax.ws.rs.core.Response;

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
