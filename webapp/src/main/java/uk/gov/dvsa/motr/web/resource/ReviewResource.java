package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.PendingSubscriptionService;
import uk.gov.dvsa.motr.web.cookie.EmailConfirmationParams;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.EmailValidator;
import uk.gov.dvsa.motr.web.validator.PhoneNumberValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ReviewResource.class);

    private final TemplateEngine renderer;
    private PendingSubscriptionService pendingSubscriptionService;
    private final MotrSession motrSession;

    @Inject
    public ReviewResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            PendingSubscriptionService pendingSubscriptionService
    ) {

        this.renderer = renderer;
        this.pendingSubscriptionService = pendingSubscriptionService;
        this.motrSession = motrSession;
    }

    @GET
    public Response reviewPage() throws Exception {

        if (!this.motrSession.isAllowedOnReviewPage()) {
            return redirect("/");
        }

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        String contactFromSession;
        String contactTypeFromSession;

        String regNumberFromSession = this.motrSession.getVrmFromSession();
        if (motrSession.isUsingEmailChannel()) {
            contactFromSession = this.motrSession.getEmailFromSession();
            contactTypeFromSession = "Email address";
            map.put("changeContactUrl", "/email");
            map.put("correctContactType", "email address");
        } else if (motrSession.isUsingSmsChannel()) {
            contactFromSession = this.motrSession.getPhoneNumberFromSession();
            contactTypeFromSession = "Mobile number";
            map.put("changeContactUrl", "/phone-number");
            map.put("correctContactType", "mobile number");
        } else {
            contactFromSession = "";
            contactTypeFromSession = "";
            map.put("changeContactUrl", "/");
            map.put("correctContactType", "");
        }

        VehicleDetails vehicle = this.motrSession.getVehicleDetailsFromSession();

        if (null != vehicle) {
            logger.info("review page resource vehicle.getMotIdentification().getDvlaId().isPresent() has value: " +
                    vehicle.getMotIdentification().getDvlaId().isPresent());

            viewModel.setColour(vehicle.getPrimaryColour(), vehicle.getSecondaryColour())
                    .setContact(contactFromSession)
                    .setContactType(contactTypeFromSession)
                    .setExpiryDate(vehicle.getMotExpiryDate())
                    .setMake(vehicle.getMake())
                    .setModel(vehicle.getModel())
                    .setRegistration(regNumberFromSession)
                    .setYearOfManufacture(vehicle.getYearOfManufacture() == null ? null : vehicle.getYearOfManufacture().toString());
            map.put("isDvlaVehicle", vehicle.getMotIdentification().getDvlaId().isPresent());
        } else {
            logger.debug("vehicle is null on get request");
            throw new NotFoundException();
        }

        this.motrSession.setVisitingFromReview(true);

        map.put("viewModel", viewModel);

        return Response.ok(renderer.render("review", map)).build();
    }

    @POST
    public Response confirmationPagePost() throws Exception {

        String vrm = motrSession.getVrmFromSession();
        String contactFromSession;
        Subscription.ContactType contactType;

        if (motrSession.isUsingEmailChannel()) {
            contactFromSession = this.motrSession.getEmailFromSession();
            contactType = Subscription.ContactType.EMAIL;
        } else if (motrSession.isUsingSmsChannel()) {
            contactFromSession = this.motrSession.getPhoneNumberFromSession();
            contactType = Subscription.ContactType.MOBILE;
        } else {
            contactFromSession = "";
            contactType = null;
        }

        VehicleDetails vehicle = this.motrSession.getVehicleDetailsFromSession();

        if (detailsAreValid(vrm, contactFromSession) && null != vehicle) {
            LocalDate expiryDate = vehicle.getMotExpiryDate();
            String redirectUri =
                    pendingSubscriptionService.handlePendingSubscriptionCreation(vrm,
                    contactFromSession,
                    expiryDate,
                    vehicle.getMotIdentification(),
                    contactType);

            return redirectToSuccessScreen(redirectUri, contactFromSession);
        } else {
            logger.debug("detailsAreValid() {} or vehicle is null: {}", detailsAreValid(vrm, contactFromSession), vehicle);
            throw new NotFoundException();
        }
    }

    private Response redirectToSuccessScreen(String redirectUri, String email) {

        EmailConfirmationParams params = new EmailConfirmationParams();
        params.setEmail(email);
        motrSession.setEmailConfirmationParams(params);

        return redirect(redirectUri);
    }

    private boolean detailsAreValid(String vrm, String contact) {

        VrmValidator vrmValidator = new VrmValidator();
        if (motrSession.isUsingEmailChannel()) {
            EmailValidator validator = new EmailValidator();
            return vrmValidator.isValid(vrm) && validator.isValid(contact);
        } else if (motrSession.isUsingSmsChannel()) {
            PhoneNumberValidator validator = new PhoneNumberValidator();
            return vrmValidator.isValid(vrm) && validator.isValid(contact);
        } else {
            return false;
        }
    }
}
