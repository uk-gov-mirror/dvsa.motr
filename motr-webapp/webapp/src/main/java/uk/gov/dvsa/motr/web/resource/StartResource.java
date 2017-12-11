package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.cookie.MotrSession;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/start")
public class StartResource {

    private final MotrSession motrSession;

    @Inject
    public StartResource(
            MotrSession motrSession
    ) {

        this.motrSession = motrSession;
    }

    @GET
    public Response start() throws Exception {

        this.motrSession.setShouldClearCookies(true);
        return redirect("/vrm");
    }
}
