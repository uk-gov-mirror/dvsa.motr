package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.service.PendingSubscriptionService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.EmailValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

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

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/review")
@Produces("text/html")
public class ReviewResource {

    private final TemplateEngine renderer;
    private PendingSubscriptionService pendingSubscriptionService;
    private final VehicleDetailsClient vehicleDetailsClient;
    private final MotrSession motrSession;

    @Inject
    public ReviewResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            PendingSubscriptionService pendingSubscriptionService,
            VehicleDetailsClient vehicleDetailsClient
    ) {

        this.renderer = renderer;
        this.pendingSubscriptionService = pendingSubscriptionService;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.motrSession = motrSession;
    }

    @GET
    public Response reviewPage() throws Exception {

        if (!this.motrSession.isAllowedOnPage()) {
            return redirect("/");
        }

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        String regNumberFromSession = this.motrSession.getVrmFromSession();
        String emailFromSession = this.motrSession.getEmailFromSession();

        Optional<VehicleDetails> vehicleOptional = this.vehicleDetailsClient.fetch(regNumberFromSession);

        if (vehicleOptional.isPresent()) {
            VehicleDetails vehicle = vehicleOptional.get();
            viewModel.setColour(vehicle.getPrimaryColour(), vehicle.getSecondaryColour())
                    .setEmail(emailFromSession)
                    .setExpiryDate(vehicle.getMotExpiryDate())
                    .setMakeModel(vehicle.getMake(), vehicle.getModel())
                    .setRegistration(regNumberFromSession)
                    .setYearOfManufacture(vehicle.getYearOfManufacture().toString());
        } else {
            throw new NotFoundException();
        }

        this.motrSession.setVisitingFromReview(true);

        map.put("viewModel", viewModel);
        return Response.ok(renderer.render("review", map)).build();
    }

    @POST
    public Response confirmationPagePost() throws Exception {

        String vrm = motrSession.getVrmFromSession();
        String email = motrSession.getEmailFromSession();
        Optional<VehicleDetails> vehicle;

        if (!detailsAreValid(vrm, email)) {
            return returnUserInputError(vrm, email);
        }

        vehicle = getVehicle(vrm);

        if (!vehicle.isPresent()) {
            return returnVehicleError(vrm);
            //TODO this is to be covered in BL-4200
        }

        createPendingSubscription(vrm, email, vehicle.get().getMotExpiryDate());

        return redirect("email-confirmation-pending");
    }

    private Response returnVehicleError(String vrm) {

        return null;
    }

    private Response returnUserInputError(String vrm, String email) {

        return null;
    }

    private boolean detailsAreValid(String vrm, String email) {

        VrmValidator vrmValidator = new VrmValidator();
        EmailValidator emailValidator = new EmailValidator();

        return vrmValidator.isValid(vrm) && emailValidator.isValid(email);
    }

    private Optional<VehicleDetails> getVehicle(String vrm) {

        try {
            return vehicleDetailsClient.fetch(vrm);
        } catch (VehicleDetailsClientException exception) {
            return Optional.empty();
        }
    }

    private void createPendingSubscription(String vrm, String email, LocalDate expiryDate) throws Exception {

        try {
            pendingSubscriptionService.createPendingSubscription(vrm, email, expiryDate);

        } catch (SubscriptionAlreadyExistsException subscriptionExistsException) {
            throw new NotFoundException();
        } 
    }
}
