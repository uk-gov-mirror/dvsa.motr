package uk.gov.dvsa.motr.web.resource;

import org.apache.commons.lang3.StringUtils;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.time.format.DateTimeFormatter;
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
@Path("/test-expired")
@Produces("text/html")
public class TestExpiredResource {

    public static final String TEST_EXPIRY_DATE_KEY = "testExpiryDate";
    public static final String IS_TEST_EXPIRED_KEY = "isTestExpired";
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

        modelMap.put(TEST_EXPIRY_DATE_KEY, formatTestExpiryDate(vehicle));
        modelMap.put(IS_TEST_EXPIRED_KEY, determineIsTestExpired(vehicle));
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
}
