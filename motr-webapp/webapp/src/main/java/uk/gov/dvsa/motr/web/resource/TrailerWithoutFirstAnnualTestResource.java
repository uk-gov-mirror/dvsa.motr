package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageId;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageType;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.util.HashMap;
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
@Path(TrailerWithoutFirstAnnualTestResource.TRAILER_WITHOUT_FIRST_ANNUAL_TEST_PATH)
@Produces("text/html")
public class TrailerWithoutFirstAnnualTestResource {

    public static final String TRAILER_WITHOUT_FIRST_ANNUAL_TEST_HEADER = "This trailer hasn't had its first annual test.";
    public static final String TRAILER_WITHOUT_FIRST_ANNUAL_TEST_CONTENT =
            "Trailers must be tested one year after they were first sold or supplied.";

    public static final String TRAILER_WITHOUT_FIRST_ANNUAL_TEST_PATH = "/trailer-without-first-annual-test";
    public static final String TRAILER_WITHOUT_FIRST_ANNUAL_TEST_TEMPLATE = "trailer-without-first-annual-test";
    public static final String HEADER_TEXT_KEY = "headerText";
    public static final String CONTENT_TEXT_ARRAY_KEY = "contentText";
    public static final String BACK_URL_KEY = "back_url";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final DataLayerHelper dataLayerHelper;

    @Inject
    public TrailerWithoutFirstAnnualTestResource(
            MotrSession motrSession,
            TemplateEngine renderer
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    public Response trailerTestExpiryUnknownPageGet() {

        if (!motrSession.isAllowedOnUnknownTestDatePage() || !motrSession.isTrailersFeatureToggleOn()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        VehicleDetails vehicleDetails = motrSession.getVehicleDetailsFromSession();
        dataLayerHelper.putAttribute(VRM_KEY, motrSession.getVrmFromSession());
        dataLayerHelper.setVehicleDataOrigin(vehicleDetails);
        dataLayerHelper.setMessage(DataLayerMessageId.TRAILER_WITHOUT_FIRST_ANNUAL_TEST,
                DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                getDataLayerMessageText());

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put(BACK_URL_KEY, HomepageResource.HOMEPAGE_URL);
        modelMap.put(HEADER_TEXT_KEY, TRAILER_WITHOUT_FIRST_ANNUAL_TEST_HEADER);
        modelMap.put(CONTENT_TEXT_ARRAY_KEY, TRAILER_WITHOUT_FIRST_ANNUAL_TEST_CONTENT);
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(TRAILER_WITHOUT_FIRST_ANNUAL_TEST_TEMPLATE, modelMap)).build();
    }

    private String getDataLayerMessageText() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(TRAILER_WITHOUT_FIRST_ANNUAL_TEST_HEADER);
        stringJoiner.add(TRAILER_WITHOUT_FIRST_ANNUAL_TEST_CONTENT);

        return stringJoiner.toString();
    }

}
