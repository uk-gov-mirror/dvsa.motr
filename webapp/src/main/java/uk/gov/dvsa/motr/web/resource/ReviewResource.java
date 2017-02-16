package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.EmailValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.net.URI;
import java.time.LocalDate;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

import static javax.ws.rs.core.Response.Status.FOUND;

@Singleton
@Path("/review")
@Produces("text/html")
public class ReviewResource {

    private final TemplateEngine renderer;
    private final SubscriptionService subscriptionService;
    private final VehicleDetailsClient vehicleDetailsClient;
    private final String baseUrl;

    @Inject
    public ReviewResource(
            TemplateEngine renderer,
            SubscriptionService subscriptionService,
            VehicleDetailsClient vehicleDetailsClient,
            @SystemVariableParam(BASE_URL) String baseUrl
    ) {

        this.renderer = renderer;
        this.subscriptionService = subscriptionService;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.baseUrl = baseUrl;
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
    public Response reviewPagePost() throws Exception {

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        // TODO replace hard coded values with session information
        VrmValidator vrmValidator = new VrmValidator();
        EmailValidator emailValidator = new EmailValidator();

        if (vrmValidator.isValid("test-reg") && emailValidator.isValid("test@test.com")) {

            try {
                Optional<VehicleDetails> vehicle = this.vehicleDetailsClient.fetch("test-reg");

                if (vehicle.isPresent()) {
                    VehicleDetails vehicleDetails = vehicle.get();
                    viewModel.setColour(vehicleDetails.getPrimaryColour(), vehicleDetails.getSecondaryColour())
                            .setEmail("test@test.com")
                            .setExpiryDate(vehicleDetails.getMotExpiryDate())
                            .setMakeModel(vehicleDetails.getMake(), vehicleDetails.getModel())
                            .setRegistration("test-reg")
                            .setYearOfManufacture(vehicleDetails.getYearOfManufacture().toString());
                }
            } catch (VehicleDetailsClientException exception) {
                //TODO this is to be covered in BL-4200
                //we will show a something went wrong banner message, so we will thread that
                //through from here
            }

            try {
                // TODO replace hard coded values with vehicle data
                this.subscriptionService.createSubscription("new-fake-reg", "thisNewEmailHere@test.com", LocalDate.of(2017, 2, 2));
                return Response.status(FOUND).location(UriBuilder.fromUri(new URI(this.baseUrl)).path("subscription-confirmation").build())
                        .build();
            } catch (SubscriptionAlreadyExistsException e) {
                throw new NotFoundException();
            }
        }

        map.put("viewModel", viewModel);

        return Response.ok().entity(renderer.render("review", map)).build();
    }
}
