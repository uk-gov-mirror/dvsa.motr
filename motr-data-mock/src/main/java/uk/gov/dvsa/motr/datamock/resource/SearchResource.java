package uk.gov.dvsa.motr.datamock.resource;

import uk.gov.dvsa.motr.datamock.model.ByDvlaIdResolver;
import uk.gov.dvsa.motr.datamock.model.ByTestNumberResolver;
import uk.gov.dvsa.motr.datamock.model.ByVrmResolver;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Singleton
@Path("/mot-test-reminder-mock/motr/v2/search")
@Produces("application/json")
public class SearchResource {

    @GET
    public Response searchVrm(
            @QueryParam("vrm") String vrm,
            @QueryParam("testNumber") String testNumber,
            @QueryParam("dvlaId") String dvlaId) {

        if (vrm != null) {
            return new ByVrmResolver().resolve(vrm);
        }

        if (testNumber != null) {
            return new ByTestNumberResolver().resolve(testNumber);
        }

        if (dvlaId != null) {
            return new ByDvlaIdResolver().resolve(dvlaId);
        }

        return Response.status(501).build();
    }
}
