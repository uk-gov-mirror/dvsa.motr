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
@Path("/mot-test-reminder-mock/motr/v2/search")
@Produces("application/json")
public class SearchResource {

    @GET
    @Path("/registration/{vrm}")
    public Response searchVrm(@PathParam("vrm") String vrm) {

        return new ByVrmResolver().resolve(vrm);
    }

    @GET
    @Path("/mot-test/{number}")
    public Response searchMotTestNumber(@PathParam("number") String number) {

        return new ByTestNumberResolver().resolve(number);
    }

    @GET
    @Path("/dvla-id/{dvlaId}")
    public Response searchDvlaId(@PathParam("dvlaId") String dvlaId) {

        return new ByDvlaIdResolver().resolve(dvlaId);
    }

    @GET
    @Path("/commercial/registration/{vrm}")
    public Response searchCommercial(@PathParam("vrm") String vrm) {

        return new ByVrmResolver().resolve(vrm);
    }
}

