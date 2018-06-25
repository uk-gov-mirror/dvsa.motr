package uk.gov.dvsa.motr.web.resource;

import com.amazonaws.util.StringUtils;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.response.PendingSubscriptionServiceResponse;
import uk.gov.dvsa.motr.web.component.subscription.service.PendingSubscriptionService;
import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.SubscriptionConfirmationParams;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.ContactDetailValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

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
    private final PendingSubscriptionService pendingSubscriptionService;
    private final SmsConfirmationService smsConfirmationService;
    private final MotrSession motrSession;
    private final ContactDetailValidator contactDetailValidator;

    @Inject
    public ReviewResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            PendingSubscriptionService pendingSubscriptionService,
            ContactDetailValidator contactDetailValidator,
            SmsConfirmationService smsConfirmationService
    ) {

        this.renderer = renderer;
        this.pendingSubscriptionService = pendingSubscriptionService;
        this.smsConfirmationService = smsConfirmationService;
        this.motrSession = motrSession;
        this.contactDetailValidator = contactDetailValidator;
    }

    @GET
    public Response reviewPage() throws Exception {

        if (!this.motrSession.isAllowedOnReviewPage()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        VehicleDetails vehicle = motrSession.getVehicleDetailsFromSession();
        ContactDetail contactDetail = motrSession.getContactDetailFromSession();
        String vrmFromSession = motrSession.getVrmFromSession();

        if (null != vehicle) {
            logger.info("review page resource vehicle.getMotIdentification().getDvlaId().isPresent() has value: " +
                    vehicle.getMotIdentification().getDvlaId().isPresent());

            viewModel.setColour(vehicle.getPrimaryColour(), vehicle.getSecondaryColour())
                    .setContact(contactDetail.getValue())
                    .setExpiryDate(vehicle.getMotExpiryDate())
                    .setMake(vehicle.getMake())
                    .setModel(vehicle.getModel())
                    .setMakeInFull(vehicle.getMakeInFull())
                    .setRegistration(vrmFromSession)
                    .setEmailChannel(motrSession.isUsingEmailChannel())
                    .setMobileChannel(motrSession.isUsingSmsChannel())
                    .setDvlaVehicle(vehicle.getMotIdentification().getDvlaId().isPresent())
                    .setYearOfManufacture(vehicle.getYearOfManufacture() == null ? null : vehicle.getYearOfManufacture().toString())
                    .setVehicleType(vehicle.getVehicleType())
                    .setHgvPsvToggle(motrSession.isHgvPsvVehiclesFeatureToggleOn())
                    .setHasTests(vehicle.getMotTestNumber() != null && !vehicle.getMotTestNumber().isEmpty());

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
        ContactDetail contactDetail = motrSession.getContactDetailFromSession();
        VehicleDetails vehicle = motrSession.getVehicleDetailsFromSession();

        if (detailsAreValid(vrm, contactDetail) && null != vehicle) {

            PendingSubscriptionServiceResponse pendingSubscriptionResponse = pendingSubscriptionService.handlePendingSubscriptionCreation(
                    vrm,
                    contactDetail,
                    vehicle.getMotExpiryDate(),
                    vehicle.getMotIdentification(),
                    vehicle.getVehicleType()
            );

            String redirectUri = pendingSubscriptionResponse.getRedirectUri();

            if (!StringUtils.isNullOrEmpty(pendingSubscriptionResponse.getConfirmationId())) {

                motrSession.setConfirmationId(pendingSubscriptionResponse.getConfirmationId());

                redirectUri = smsConfirmationService.handleSmsConfirmationCreation(
                        vrm,
                        contactDetail.getValue(),
                        pendingSubscriptionResponse.getConfirmationId());
            }

            return redirectToNextScreen(redirectUri, contactDetail, vrm, vehicle.getVehicleType());
        } else {
            logger.debug("detailsAreValid() {} or vehicle is null: {}", detailsAreValid(vrm, contactDetail), vehicle);
            throw new NotFoundException();
        }
    }

    private Response redirectToNextScreen(String redirectUri, ContactDetail contactDetail, String vrm, VehicleType vehicleType) {

        SubscriptionConfirmationParams params = new SubscriptionConfirmationParams();
        params.setRegistration(vrm);
        params.setContact(contactDetail.getValue());
        params.setContactType(contactDetail.getContactType().getValue());
        params.setVehicleType(vehicleType);
        motrSession.setSubscriptionConfirmationParams(params);

        return redirect(redirectUri);
    }

    private boolean detailsAreValid(String vrm, ContactDetail contactDetail) {

        VrmValidator vrmValidator = new VrmValidator();
        return vrmValidator.isValid(vrm) && contactDetailValidator.isValid(contactDetail);
    }
}
