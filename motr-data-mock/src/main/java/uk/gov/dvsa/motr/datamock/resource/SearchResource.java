package uk.gov.dvsa.motr.datamock.resource;

import uk.gov.dvsa.motr.datamock.model.ByVrmResolver;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Singleton
@Path("/mot-test-reminder-mock/motr/v2/search")
@Produces("application/json")
public class SearchResource {

    @GET
    @Path("/registration/{vrm}")
    public Response searchVrm(@PathParam("vrm") String vrm) {

        return new ByVrmResolver().resolve(vrm);
    }
}

