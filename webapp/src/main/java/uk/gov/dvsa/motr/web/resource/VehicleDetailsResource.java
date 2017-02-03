package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.service.VehicleService;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Singleton
@Path("/vehicle-details")
@Produces("text/html")
public class VehicleDetailsResource {

    private static final String VEHICLE_DETAILS_KEY = "vehicleDetails";
    private static final String VEHICLE_DETAILS_COOKIE_KEY = "vehicleDetailsCookie";

    private final TemplateEngine renderer;
    private final VehicleService vehicleService;

    @Inject
    public VehicleDetailsResource(TemplateEngine renderer, VehicleService vehicleService) {

        this.renderer = renderer;
        this.vehicleService = vehicleService;
    }

    @GET
    public String vehicleDetailsPageGetRequest(@CookieParam(VEHICLE_DETAILS_COOKIE_KEY) String vehicleDetailsCookie) throws Exception {

        Map<String, String> vehicleDetailsMap = new HashMap<>();

        if (vehicleDetailsCookie != null) {
            vehicleDetailsMap.put(VEHICLE_DETAILS_KEY, vehicleDetailsCookie);
        }

        return renderer.render("vehicle-details", vehicleDetailsMap);
    }

    @POST
    public Response vehicleDetailsPagePostRequest(@FormParam("regNumber") String vrm)  {

        Map<String, Object> vehicleDetailsMap = this.vehicleService.createVehicleResponseMap(vrm);

        return Response.status(200)
                .cookie(new NewCookie(VEHICLE_DETAILS_COOKIE_KEY, vrm))
                .entity(renderer.render("vehicle-details", vehicleDetailsMap))
                .build();
    }
}
