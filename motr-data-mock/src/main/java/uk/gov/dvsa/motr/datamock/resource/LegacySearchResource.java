package uk.gov.dvsa.motr.datamock.resource;

import uk.gov.dvsa.motr.datamock.model.ByDvlaIdResolver;
import uk.gov.dvsa.motr.datamock.model.ByTestNumberResolver;
import uk.gov.dvsa.motr.datamock.model.ByVrmResolver;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Singleton
@Path("/mot-test-reminder-mock")
@Produces("application/json")
public class LegacySearchResource {

    @GET
    @Path("/mot-tests/{testNumber}")
    public Response searchTestNumber(@PathParam("testNumber") String testNumber) {

        return new ByTestNumberResolver().resolve(testNumber);
    }

    @GET
    @Path("/mot-tests-by-dvla-id/{dvlaId}")
    public Response searchDvlaId(@PathParam("dvlaId") String dvlaId) {

        return new ByDvlaIdResolver().resolve(dvlaId);
    }

    @GET
    @Path("/vehicles/{vrm}")
    public Response searchVrm(@PathParam("vrm") String vrm) {

        return new ByVrmResolver().resolve(vrm);
    }
}
