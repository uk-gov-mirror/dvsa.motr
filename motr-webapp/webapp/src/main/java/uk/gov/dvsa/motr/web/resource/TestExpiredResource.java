package uk.gov.dvsa.motr.web.resource;

import org.apache.commons.lang3.StringUtils;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageId;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageType;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.time.format.DateTimeFormatter;
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

    public static final List<String> ANNUAL_EXPIRED_CONTENT = Arrays.asList(
            "You must get the annual test done before you can sign up to get free reminders",
            "You can get a court imposed fine for driving without a valid annual test",
            "If the vehicle has been tested recently, it can take up to 10 working days for us to update our records"
    );

    public static final String HEADER_TEXT_KEY = "headerText";
    public static final String CONTENT_TEXT_ARRAY_KEY = "contentTextArray";
    public static final String TEST_EXPIRY_DATE_FORMAT = "dd MMMM yyyy";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final DataLayerHelper dataLayerHelper;

    @Inject
    public TestExpiredResource(
            MotrSession motrSession,
            TemplateEngine renderer
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    public Response testExpiredPageGet() throws Exception {

        if (!motrSession.isAllowedOnTestExpiredPage()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        dataLayerHelper.putAttribute(VRM_KEY, motrSession.getVrmFromSession());

        VehicleDetails vehicle = motrSession.getVehicleDetailsFromSession();
        Map<String, Object> modelMap = new HashMap<>();
        dataLayerHelper.setVehicleDataOrigin(vehicle);
        dataLayerHelper.setMessage(getDataLayerMessageId(vehicle),
                DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                getDataLayerMessageText(vehicle));

        modelMap.put(HEADER_TEXT_KEY, getHeaderText(vehicle));
        modelMap.put(CONTENT_TEXT_ARRAY_KEY, getPageContent(vehicle));
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

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

        return vehicle.getVehicleType() == VehicleType.MOT ? "mot-test-expired" : "annual-test-expired";
    }

    private DataLayerMessageId getDataLayerMessageId(VehicleDetails vehicle) {

        if (determineIsTestExpired(vehicle)) {
            return vehicle.getVehicleType() == VehicleType.MOT
                    ? DataLayerMessageId.VEHICLE_MOT_TEST_EXPIRED
                    : DataLayerMessageId.VEHICLE_ANNUAL_TEST_EXPIRED;
        }

        return vehicle.getVehicleType() == VehicleType.MOT
                ? DataLayerMessageId.VEHICLE_MOT_TEST_DUE
                : DataLayerMessageId.VEHICLE_ANNUAL_TEST_DUE;
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

        return ANNUAL_EXPIRED_CONTENT;
    }

    private String getHeaderText(VehicleDetails vehicle) {

        String testType = vehicle.getVehicleType() == VehicleType.MOT ? "MOT" : "annual";

        return determineIsTestExpired(vehicle)
                ? String.format("This vehicle’s %s test expired on %s", testType, formatTestExpiryDate(vehicle))
                : String.format("First %s test was due %s", testType, formatTestExpiryDate(vehicle));
    }

}
