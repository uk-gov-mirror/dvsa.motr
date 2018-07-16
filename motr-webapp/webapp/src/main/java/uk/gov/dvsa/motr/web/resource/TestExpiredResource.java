package uk.gov.dvsa.motr.web.resource;

import org.apache.commons.lang3.StringUtils;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageId;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageType;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;
import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/test-expired")
@Produces("text/html")
public class TestExpiredResource {

    public static final List<String> MOT_EXPIRED_CONTENT = Arrays.asList(
            "You must get the MOT test done before you can sign up to get free reminders",
            "You can be fined up to £1,000 for driving a vehicle without a valid MOT"
    );

    public static List<String> ANNUAL_EXPIRED_CONTENT = new ArrayList<>(Arrays.asList(
            "You must get the annual test done before you can sign up to get free reminders",
            "You can get a court imposed fine for driving without a valid annual test"
    ));

    public static String ANNUAL_EXPIRED_SUMMARY_CONTENT =
            "If the %s has been tested recently, it can take up to 10 working days for us to update our records";

    public static final String HEADER_TEXT_KEY = "headerText";
    public static final String CONTENT_TEXT_ARRAY_KEY = "contentTextArray";
    public static final String VEHICLE_DESCRIPTIVE_TYPE_KEY = "vehicleDescriptiveType";
    public static final String TEST_EXPIRY_DATE_FORMAT = "dd MMMM yyyy";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final DataLayerHelper dataLayerHelper;
    private final SmartSurveyFeedback smartSurveyHelperFeedback;

    @Inject
    public TestExpiredResource(
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
    public Response testExpiredPageGet() throws Exception {

        if (!motrSession.isAllowedOnTestExpiredPage()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        VehicleDetails vehicle = motrSession.getVehicleDetailsFromSession();
        Map<String, Object> modelMap = new HashMap<>();

        dataLayerHelper.putAttribute(VRM_KEY, motrSession.getVrmFromSession());
        dataLayerHelper.setVehicleDataOrigin(vehicle);
        dataLayerHelper.setMessage(getDataLayerMessageId(vehicle),
                DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                getDataLayerMessageText(vehicle));

        smartSurveyHelperFeedback.addVrm(vehicle.getRegNumber());
        smartSurveyHelperFeedback.addVehicleType(vehicle.getVehicleType());
        smartSurveyHelperFeedback.addIsSigningBeforeFirstMotDue(vehicle.hasNoMotYet());

        modelMap.put(HEADER_TEXT_KEY, getHeaderText(vehicle));
        modelMap.put(CONTENT_TEXT_ARRAY_KEY, getPageContent(vehicle));
        modelMap.put(VEHICLE_DESCRIPTIVE_TYPE_KEY, getVehicleDescriptiveType(vehicle));
        modelMap.putAll(dataLayerHelper.formatAttributes());
        modelMap.putAll(smartSurveyHelperFeedback.formatAttributes());
        dataLayerHelper.clear();
        smartSurveyHelperFeedback.clear();

        String templateName = determineTemplateName(vehicle);

        return Response.ok(renderer.render(templateName, modelMap)).build();
    }

    private String formatTestExpiryDate(VehicleDetails vehicle) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TEST_EXPIRY_DATE_FORMAT);

        return vehicle.getMotExpiryDate().format(dateTimeFormatter);
    }

    private boolean determineIsTestExpired(VehicleDetails vehicle) {

        return !StringUtils.isEmpty(vehicle.getMotTestNumber());
    }

    private String determineTemplateName(VehicleDetails vehicle) {
        if (vehicle.getVehicleType() == VehicleType.MOT) {
            return "mot-test-expired";
        }

        return "annual-test-expired";
    }

    private DataLayerMessageId getDataLayerMessageId(VehicleDetails vehicle) {

        if (determineIsTestExpired(vehicle)) {
            if (vehicle.getVehicleType() == VehicleType.MOT) {
                return DataLayerMessageId.VEHICLE_MOT_TEST_EXPIRED;
            } else if (VehicleType.isTrailer(vehicle.getVehicleType())) {
                return DataLayerMessageId.TRAILER_ANNUAL_TEST_EXPIRED;
            }

            return DataLayerMessageId.VEHICLE_ANNUAL_TEST_EXPIRED;
        }

        if (vehicle.getVehicleType() == VehicleType.MOT) {
            return DataLayerMessageId.VEHICLE_MOT_TEST_DUE;
        } else if (VehicleType.isTrailer(vehicle.getVehicleType())) {
            return DataLayerMessageId.TRAILER_ANNUAL_TEST_DUE;
        }

        return DataLayerMessageId.VEHICLE_ANNUAL_TEST_DUE;
    }

    private String getDataLayerMessageText(VehicleDetails vehicle) {

        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(getHeaderText(vehicle));
        getPageContent(vehicle).forEach(stringJoiner::add);

        return stringJoiner.toString();
    }

    private List<String> getPageContent(VehicleDetails vehicle) {
        if (vehicle.getVehicleType() == VehicleType.MOT) {
            return MOT_EXPIRED_CONTENT;
        }

        List<String> annualExpiredFullContent = new ArrayList<>(ANNUAL_EXPIRED_CONTENT);
        annualExpiredFullContent.add(
                String.format(ANNUAL_EXPIRED_SUMMARY_CONTENT, getVehicleDescriptiveType(vehicle)));

        return annualExpiredFullContent;
    }

    private String getVehicleDescriptiveType(VehicleDetails vehicle) {
        if (VehicleType.isTrailer(vehicle.getVehicleType())) {
            return "trailer";
        }

        return "vehicle";
    }

    private String getHeaderText(VehicleDetails vehicle) {

        String testType = vehicle.getVehicleType() == VehicleType.MOT ? "MOT" : "annual";

        return determineIsTestExpired(vehicle)
                ? String.format("This %s’s %s test expired on %s",
                getVehicleDescriptiveType(vehicle), testType, formatTestExpiryDate(vehicle))
                : String.format("First %s test was due %s", testType, formatTestExpiryDate(vehicle));
    }

}
