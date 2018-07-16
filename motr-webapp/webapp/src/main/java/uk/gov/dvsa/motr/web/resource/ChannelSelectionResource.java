package uk.gov.dvsa.motr.web.resource;

import org.apache.commons.lang3.StringUtils;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageId;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageType;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.ChannelSelectionValidator;

import java.util.HashMap;
import java.util.Map;

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
@Path("/channel-selection")
@Produces("text/html")
public class ChannelSelectionResource {

    private static final String CHANNEL_SELECTION_TEMPLATE = "channel-selection";
    private static final String MESSAGE_MODEL_KEY = "message";
    private static final String INPUT_FIELD_ID = "channel-selection-input";
    private static final String INPUT_FIELD_ID_MODEL_KEY = "inputFieldId";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final DataLayerHelper dataLayerHelper;
    private final SmartSurveyFeedback smartSurveyFeedback;

    @Inject
    public ChannelSelectionResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            SmartSurveyFeedback smartSurveyFeedback
    ) {
        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
        this.smartSurveyFeedback = smartSurveyFeedback;
    }

    @GET
    public Response channelSelectionPageGet() throws Exception {

        if (!motrSession.isAllowedOnChannelSelectionPage()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("isEmailChecked", (motrSession.getChannelFromSession().equals("email")));
        modelMap.put("isTextChecked", (motrSession.getChannelFromSession().equals("text")));
        modelMap.put("continue_button_text", "Continue");
        modelMap.put("back_button_text", "Back");
        modelMap.put("back_url", "vrm");

        addDetailsForSurveyFromSession(modelMap);

        return Response.ok(renderer.render(CHANNEL_SELECTION_TEMPLATE, modelMap)).build();
    }

    @POST
    public Response channelSelectionPagePost(@FormParam("selectChannel") String formParamChannel) throws Exception {

        ChannelSelectionValidator channelSelectionValidator = new ChannelSelectionValidator();

        if (channelSelectionValidator.isValid(formParamChannel)) {
            if (formParamChannel.equals("email")) {
                motrSession.setChannel("email");

                return redirect("email");
            } else if (formParamChannel.equals("text")) {
                motrSession.setChannel("text");

                return redirect("phone-number");
            }
        }

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put(MESSAGE_MODEL_KEY, channelSelectionValidator.getMessage());
        dataLayerHelper.setMessage(DataLayerMessageId.CHANNEL_SELECTION_VALIDATION_ERROR,
                DataLayerMessageType.USER_INPUT_ERROR,
                channelSelectionValidator.getMessage());

        modelMap.put("isEmailChecked", (motrSession.getChannelFromSession().equals("email")));
        modelMap.put("isTextChecked", (motrSession.getChannelFromSession().equals("text")));
        modelMap.put("continue_button_text", "Continue");
        modelMap.put("back_button_text", "Back");
        modelMap.put("back_url", "vrm");
        modelMap.put(INPUT_FIELD_ID_MODEL_KEY, INPUT_FIELD_ID);

        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        addDetailsForSurveyFromSession(modelMap);

        return Response.ok(renderer.render(CHANNEL_SELECTION_TEMPLATE, modelMap)).build();
    }

    private void addDetailsForSurveyFromSession(Map<String, Object> modelMap) {

        VehicleDetails vehicle = motrSession.getVehicleDetailsFromSession();

        if (!StringUtils.isEmpty(motrSession.getChannelFromSession())) {
            smartSurveyFeedback.addContactType(motrSession.getChannelFromSession().equals("email")
                    ? Subscription.ContactType.EMAIL.getValue() : Subscription.ContactType.MOBILE.getValue());
        }
        smartSurveyFeedback.addVrm(vehicle.getRegNumber());
        smartSurveyFeedback.addVehicleType(vehicle.getVehicleType());
        smartSurveyFeedback.addIsSigningBeforeFirstMotDue(vehicle.hasNoMotYet());

        modelMap.putAll(smartSurveyFeedback.formatAttributes());
        smartSurveyFeedback.clear();
    }
}
