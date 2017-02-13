package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/review")
@Produces("text/html")
public class ReviewResource {

    private final TemplateEngine renderer;
    private VehicleDetailsClient vehicleDetailsClient;

    @Inject
    public ReviewResource(TemplateEngine renderer, VehicleDetailsClient vehicleDetailsClient) {
        this.renderer = renderer;
        this.vehicleDetailsClient = vehicleDetailsClient;
    }

    @GET
    public String reviewPage() throws Exception {

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        //TODO Should be the vrn from the cookie when it's ready
        Optional<VehicleDetails> vehicleOptional = this.vehicleDetailsClient.fetch("YN13NTX");

        if (vehicleOptional.isPresent()) {
            VehicleDetails vehicle = vehicleOptional.get();
            viewModel.setColour(vehicle.getPrimaryColour(), vehicle.getSecondaryColour())
                    .setEmail("test@test.com")
                    .setExpiryDate(vehicle.getMotExpiryDate())
                    .setMakeModel(vehicle.getMake(), vehicle.getModel())
                    .setRegistration("test-reg")
                    .setYearOfManufacture(vehicle.getYearOfManufacture().toString());
        } else {
            throw new NotFoundException();
        }

        map.put("viewModel", viewModel);

        return renderer.render("review", map);
    }

    @POST
    public String reviewPagePost() throws Exception {

        //TODO Add in validation of both VRM and EMAIL formats
        //TODO Add in call to dynamo DB to persist subscription
        //TODO Add in call to gov notify to set up the subscription.
        return renderer.render("review", emptyMap());
    }
}
