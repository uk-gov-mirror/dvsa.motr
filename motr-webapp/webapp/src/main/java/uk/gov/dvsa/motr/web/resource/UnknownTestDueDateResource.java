package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageId;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageType;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;
import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path(UnknownTestDueDateResource.UNKNOWN_TEST_DATE_PATH)
@Produces("text/html")
public class UnknownTestDueDateResource {

    public static final String UNKNOWN_TEST_DATE_PATH = "/unknown-test-due-date";
    public static final String UNKNOWN_TEST_DATE_TEMPLATE = "unknown-test-due-date";
    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final DataLayerHelper dataLayerHelper;
    private final SmartSurveyFeedback smartSurveyHelperFeedback;

    @Inject
    public UnknownTestDueDateResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            SmartSurveyFeedback smartSurveyHelperFeedback
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
        this.smartSurveyHelperFeedback = smartSurveyHelperFeedback;
    }

    @GET
    public Response testExpiryUnknownPageGet() {

        if (!motrSession.isAllowedOnUnknownTestDatePage()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        VehicleDetails vehicleDetails = motrSession.getVehicleDetailsFromSession();
        dataLayerHelper.putAttribute(VRM_KEY, motrSession.getVrmFromSession());
        dataLayerHelper.setVehicleDataOrigin(vehicleDetails);
        dataLayerHelper.setMessage(DataLayerMessageId.ANNUAL_TEST_DATE_UNKNOWN,
                DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                "We don't know when this vehicle's first annual test is due");
        smartSurveyHelperFeedback.addVrm(vehicleDetails.getRegNumber());
        smartSurveyHelperFeedback.addVehicleType(vehicleDetails.getVehicleType());

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("back_url", HomepageResource.HOMEPAGE_URL);
        modelMap.putAll(dataLayerHelper.formatAttributes());
        modelMap.putAll(smartSurveyHelperFeedback.formatAttributes());
        dataLayerHelper.clear();
        smartSurveyHelperFeedback.clear();

        return Response.ok(renderer.render(UNKNOWN_TEST_DATE_TEMPLATE, modelMap)).build();
    }
}
