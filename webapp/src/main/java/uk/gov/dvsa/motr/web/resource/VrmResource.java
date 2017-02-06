package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.VrmValidator;

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

import static java.util.Collections.emptyMap;

@Singleton
@Path("/vrm")
@Produces("text/html")
public class VrmResource {

    private static final String VRM_MODEL_KEY = "vrm";
    private static final String VEHICLE_NOT_FOUND_MESSAGE = "Check that you’ve typed in the correct registration number.<br/>" +
            "<br/>You can only sign up if your vehicle has had its first MOT.";

    private static final String MESSAGE_KEY = "message";
    private static final String SHOW_INLINE_KEY = "showInLine";

    private final TemplateEngine renderer;
    private final VehicleDetailsClient client;

    @Inject
    public VrmResource(TemplateEngine renderer, VehicleDetailsClient client) {

        this.renderer = renderer;
        this.client = client;
    }

    @GET
    public String vrmPageGet() throws Exception {

        return renderer.render("vrm", emptyMap());
    }

    @POST
    public String vrmPagePost(@FormParam("regNumber") String formParamVrm) {

        String vrm = normalizeFormInputVrm(formParamVrm);

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put(VRM_MODEL_KEY, vrm);
        modelMap.put(SHOW_INLINE_KEY, true);

        VrmValidator validator = new VrmValidator();

        if (validator.isValid(vrm)) {

            try {

                Optional<VehicleDetails> vehicle = this.client.fetch(vrm);
                if (!vehicle.isPresent()) {
                    modelMap.put(MESSAGE_KEY, VEHICLE_NOT_FOUND_MESSAGE);
                    modelMap.put(SHOW_INLINE_KEY, false);
                }

            } catch (VehicleDetailsClientException exception) {
                //TODO this is to be covered in BL-4200
                //we will show a something went wrong banner message, so we will thread that
                //through from here
            }

        } else {

            modelMap.put(MESSAGE_KEY, validator.getMessage());
        }

        return renderer.render("vrm", modelMap);
    }

    private static String normalizeFormInputVrm(String formInput) {

        return formInput.replaceAll("\\s+", "").toUpperCase();
    }
}
