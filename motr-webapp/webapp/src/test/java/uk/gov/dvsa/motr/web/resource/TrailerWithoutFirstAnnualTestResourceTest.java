package uk.gov.dvsa.motr.web.resource;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageId;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageType;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.resource.HomepageResource.HOMEPAGE_URL;
import static uk.gov.dvsa.motr.web.resource.TrailerWithoutFirstAnnualTestResource.CONTENT_TEXT_ARRAY_KEY;
import static uk.gov.dvsa.motr.web.resource.TrailerWithoutFirstAnnualTestResource.HEADER_TEXT_KEY;
import static uk.gov.dvsa.motr.web.resource.TrailerWithoutFirstAnnualTestResource.TRAILER_WITHOUT_FIRST_ANNUAL_TEST_CONTENT;
import static uk.gov.dvsa.motr.web.resource.TrailerWithoutFirstAnnualTestResource.TRAILER_WITHOUT_FIRST_ANNUAL_TEST_HEADER;
import static uk.gov.dvsa.motr.web.resource.TrailerWithoutFirstAnnualTestResource.TRAILER_WITHOUT_FIRST_ANNUAL_TEST_TEMPLATE;

public class TrailerWithoutFirstAnnualTestResourceTest {

    private static final String VRM = "TRAILER-UNKNEXP";

    private TemplateEngineStub templateEngine;
    private TrailerWithoutFirstAnnualTestResource resource;
    private MotrSession motrSession;
    private VehicleDetails vehicle;

    @Before
    public void setUp() {

        templateEngine = new TemplateEngineStub();
        motrSession = mock(MotrSession.class);
        resource = new TrailerWithoutFirstAnnualTestResource(motrSession, templateEngine);
        vehicle = new VehicleDetails();

        when(motrSession.getVrmFromSession()).thenReturn(VRM);
    }

    @Test
    public void getWhenNotAllowedOnPage_ResultsInRedirectToHomePage() {

        when(motrSession.isAllowedOnUnknownTestDatePage()).thenReturn(false);

        Response response = resource.trailerTestExpiryUnknownPageGet();

        verify(motrSession, times(0)).getVehicleDetailsFromSession();
        assertEquals(302, response.getStatus());
    }

    @Test
    public void getWhenTrailerToggleOff_ResultsInRedirectToHomePage() {

        when(motrSession.isTrailersFeatureToggleOn()).thenReturn(false);

        Response response = resource.trailerTestExpiryUnknownPageGet();

        verify(motrSession, times(0)).getVehicleDetailsFromSession();
        assertEquals(302, response.getStatus());
    }

    @Test
    public void getIsSuccessful_whenUserIsAllowedToAccessIt() {

        when(motrSession.isAllowedOnUnknownTestDatePage()).thenReturn(true);
        when(motrSession.isTrailersFeatureToggleOn()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicle);

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put(HEADER_TEXT_KEY, TRAILER_WITHOUT_FIRST_ANNUAL_TEST_HEADER);
        expectedContext.put(CONTENT_TEXT_ARRAY_KEY, TRAILER_WITHOUT_FIRST_ANNUAL_TEST_CONTENT);
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(VRM,
                        VehicleType.MOT,
                        DataLayerMessageId.TRAILER_WITHOUT_FIRST_ANNUAL_TEST,
                        DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                        getDataLayerMessageText(TRAILER_WITHOUT_FIRST_ANNUAL_TEST_HEADER, TRAILER_WITHOUT_FIRST_ANNUAL_TEST_CONTENT)));

        expectedContext.put("back_url", HOMEPAGE_URL);

        Response response = resource.trailerTestExpiryUnknownPageGet();

        assertEquals(200, response.getStatus());
        assertEquals(TRAILER_WITHOUT_FIRST_ANNUAL_TEST_TEMPLATE, templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    private String getExpectedDataLayerJsonString(String vrm, VehicleType vehicleOrigin,
                                                  DataLayerMessageId messageId,
                                                  DataLayerMessageType messageType,
                                                  String messageText) {

        JSONObject content = new JSONObject();
        content.put("vrm", vrm);
        content.put("vehicle-data-origin", vehicleOrigin);
        if (messageId != null) {
            content.put("message-id", messageId);
            content.put("message-type", messageType);
            content.put("message-text", messageText);
        }

        return content.toString();
    }

    private String getDataLayerMessageText(String header, String content) {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(header);
        stringJoiner.add(content);

        return stringJoiner.toString();
    }

}
