package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionConfirmationFailureEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionConfirmationSuccessfulEvent;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.EmailValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.net.URI;
import java.net.URISyntaxException;
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
    private final MotrSession motrSession;

    @Inject
    public ReviewResource(
            @SystemVariableParam(BASE_URL) String baseUrl,
            MotrSession motrSession,
            TemplateEngine renderer,
            SubscriptionService subscriptionService,
            VehicleDetailsClient vehicleDetailsClient
    ) {

        this.renderer = renderer;
        this.subscriptionService = subscriptionService;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.baseUrl = baseUrl;
        this.motrSession = motrSession;
    }

    @GET
    public Response reviewPage() throws Exception {

        if (!this.motrSession.isAllowedOnPage()) {
            return Response.status(Response.Status.FOUND).location(getFullUriForPage("/")).build();
        }

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        String regNumberFromSession = this.motrSession.getRegNumberFromSession();
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
        return Response.ok().entity(renderer.render("review", map)).build();
    }

    @POST
    public Response reviewPagePost() throws Exception {

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        String regNumberFromSession = this.motrSession.getRegNumberFromSession();
        String emailFromSession = this.motrSession.getEmailFromSession();

        VrmValidator vrmValidator = new VrmValidator();
        EmailValidator emailValidator = new EmailValidator();

        if (vrmValidator.isValid(regNumberFromSession) && emailValidator.isValid(emailFromSession)) {

            try {
                Optional<VehicleDetails> vehicle = this.vehicleDetailsClient.fetch(regNumberFromSession);

                if (vehicle.isPresent()) {
                    VehicleDetails vehicleDetails = vehicle.get();
                    viewModel.setColour(vehicleDetails.getPrimaryColour(), vehicleDetails.getSecondaryColour())
                            .setEmail(emailFromSession)
                            .setExpiryDate(vehicleDetails.getMotExpiryDate())
                            .setMakeModel(vehicleDetails.getMake(), vehicleDetails.getModel())
                            .setRegistration(regNumberFromSession)
                            .setYearOfManufacture(vehicleDetails.getYearOfManufacture().toString());

                    try {
                        this.subscriptionService.createSubscription(vehicleDetails.getRegNumber(), emailFromSession,
                                vehicleDetails.getMotExpiryDate());
                        EventLogger.logEvent(new SubscriptionConfirmationSuccessfulEvent().setVrm(regNumberFromSession)
                                .setEmail(emailFromSession).setExpiryDate(vehicleDetails.getMotExpiryDate()));
                        return Response.status(FOUND).location(getFullUriForPage("subscription-confirmation")).build();
                    } catch (SubscriptionAlreadyExistsException e) {
                        EventLogger.logErrorEvent(new SubscriptionConfirmationFailureEvent().setVrm(regNumberFromSession)
                                .setEmail(emailFromSession).setExpiryDate(vehicleDetails.getMotExpiryDate()), e);
                        throw new NotFoundException();
                    }
                }
            } catch (VehicleDetailsClientException exception) {
                //TODO this is to be covered in BL-4200
                //we will show a something went wrong banner message, so we will thread that
                //through from here
            }
        }

        map.put("viewModel", viewModel);

        return Response.ok().entity(renderer.render("review", map)).build();
    }

    private URI getFullUriForPage(String page) throws URISyntaxException {

        return UriBuilder.fromUri(new URI(this.baseUrl)).path(page).build();
    }
}
