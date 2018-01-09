package uk.gov.dvsa.motr.web.resource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/")
public class HomepageResource {

    public static final String HOMEPAGE_URL = "https://www.gov.uk/mot-reminder";

    @GET
    public Response homePage() throws Exception {

        return redirect(HOMEPAGE_URL, Response.Status.MOVED_PERMANENTLY);
    }
}
