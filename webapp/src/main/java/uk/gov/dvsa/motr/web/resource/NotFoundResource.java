package uk.gov.dvsa.motr.web.resource;


import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Explicit not-found resource to cater for redirections (e.g. S3 serving a static asset that does not exist will redirect to it)
 */
@Singleton
@Path("/not-found")
public class NotFoundResource {

    @GET
    public Response notFound() throws Exception {

        throw new NotFoundException();
    }
}
