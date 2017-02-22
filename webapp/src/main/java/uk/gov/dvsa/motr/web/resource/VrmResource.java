package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.VrmValidator;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/vrm")
@Produces("text/html")
public class VrmResource {

    private static final String VRM_MODEL_KEY = "vrm";
    private static final String VEHICLE_NOT_FOUND_MESSAGE = "Check that you’ve typed in the correct registration number.<br/>" +
            "<br/>You can only sign up if your vehicle has had its first MOT.";

    private static final String MESSAGE_KEY = "message";
    private static final String SHOW_INLINE_KEY = "showInLine";
    private static final String VRM_TEMPLATE_NAME = "vrm";

    private final TemplateEngine renderer;
    private final VehicleDetailsClient client;
    private final MotrSession motrSession;

    @Inject
    public VrmResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            VehicleDetailsClient client
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
        this.client = client;
    }

    @GET
    public String vrmPageGet() throws Exception {

        String regNumber = this.motrSession.getRegNumberFromSession();

        Map<String, Object> modelMap = new HashMap<>();
        updateMapBasedOnReviewFlow(modelMap);

        modelMap.put(VRM_MODEL_KEY, regNumber);

        if (this.motrSession.visitingFromReviewPage()) {
            modelMap.put("back_location", "review");
        }

        return renderer.render("vrm", modelMap);
    }

    @POST
    public Response vrmPagePost(@FormParam("regNumber") String formParamVrm) throws Exception {

        String vrm = normalizeFormInputVrm(formParamVrm);

        Map<String, Object> modelMap = new HashMap<>();
        updateMapBasedOnReviewFlow(modelMap);

        modelMap.put(VRM_MODEL_KEY, vrm);
        modelMap.put(SHOW_INLINE_KEY, true);

        VrmValidator validator = new VrmValidator();
        if (validator.isValid(vrm)) {
            try {
                Optional<VehicleDetails> vehicle = this.client.fetch(vrm);
                if (!vehicle.isPresent()) {
                    modelMap.put(MESSAGE_KEY, VEHICLE_NOT_FOUND_MESSAGE);
                    modelMap.put(SHOW_INLINE_KEY, false);
                } else {
                    this.motrSession.setVrm(vrm);
                    if (this.motrSession.visitingFromReviewPage()) {
                        return redirect("review");
                    }

                    return redirect("email");
                }
            } catch (VehicleDetailsClientException exception) {
                //TODO this is to be covered in BL-4200
                //we will show a something went wrong banner message, so we will thread that
                //through from here
                throw exception;
            }

        } else {
            modelMap.put(MESSAGE_KEY, validator.getMessage());
        }

        modelMap.put(VRM_MODEL_KEY, vrm);

        return Response.ok(renderer.render(VRM_TEMPLATE_NAME, modelMap)).build();
    }

    private static String normalizeFormInputVrm(String formInput) {

        return formInput.replaceAll("\\s+", "").toUpperCase();
    }

    private void updateMapBasedOnReviewFlow(Map<String, Object> modelMap) throws URISyntaxException {

        if (this.motrSession.visitingFromReviewPage()) {
            modelMap.put("continue_button_text", "Save and return to review");
            modelMap.put("back_button_text", "Cancel and return");
            modelMap.put("back_url", "review");
        } else {
            modelMap.put("continue_button_text", "Continue");
            modelMap.put("back_button_text", "Back");
            modelMap.put("back_url", "/");
        }
    }

}
