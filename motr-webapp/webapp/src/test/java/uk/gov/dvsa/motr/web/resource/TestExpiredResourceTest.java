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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.ANNUAL_EXPIRED_CONTENT;
import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.CONTENT_TEXT_ARRAY_KEY;
import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.HEADER_TEXT_KEY;
import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.TEST_EXPIRY_DATE_FORMAT;
import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.VEHICLE_DESCRIPTIVE_TYPE_KEY;

public class TestExpiredResourceTest {

    private static final String VRM = "VRZ5555";

    private TemplateEngineStub templateEngine;
    private TestExpiredResource resource;
    private MotrSession motrSession;
    private LocalDate testExpiryDate;
    private VehicleDetails vehicle;

    @Before
    public void setUp() {

        templateEngine = new TemplateEngineStub();
        motrSession = mock(MotrSession.class);
        resource = new TestExpiredResource(motrSession, templateEngine);
        testExpiryDate = LocalDate.now().minusDays(2);
        vehicle = new VehicleDetails();
        vehicle.setMotExpiryDate(testExpiryDate);

        when(motrSession.getVrmFromSession()).thenReturn(VRM);
    }

    @Test
    public void getWhenNotAllowedOnPageResultsInRedirectToHomePage() throws Exception {

        when(motrSession.isAllowedOnTestExpiredPage()).thenReturn(false);

        Response response = resource.testExpiredPageGet();

        verify(motrSession, times(0)).getVehicleDetailsFromSession();
        assertEquals(302, response.getStatus());
    }

    @Test
    public void getWithVehicleDetailsWithoutMotTestNumberResultsInMotTestExpiredViewAndIsTestExpiredEqualsFalse() throws Exception {

        vehicle.setVehicleType(VehicleType.MOT);

        when(motrSession.isAllowedOnTestExpiredPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicle);

        HashMap<String, Object> expectedContext = new HashMap<>();
        String headerOutput = "First MOT test was due " + formatDate(testExpiryDate);
        expectedContext.put(HEADER_TEXT_KEY, headerOutput);
        expectedContext.put(CONTENT_TEXT_ARRAY_KEY, TestExpiredResource.MOT_EXPIRED_CONTENT);
        expectedContext.put(VEHICLE_DESCRIPTIVE_TYPE_KEY, "vehicle");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(VRM,
                        VehicleType.MOT,
                        DataLayerMessageId.VEHICLE_MOT_TEST_DUE,
                        DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                        getMessageText(headerOutput, TestExpiredResource.MOT_EXPIRED_CONTENT)));

        Response response = resource.testExpiredPageGet();

        assertEquals(200, response.getStatus());
        assertEquals("mot-test-expired", templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    @Test
    public void getWithMotVehicleDetailsWithMotTestNumberResultsInMotTestExpiredViewAndIsTestExpiredEqualsTrue() throws Exception {

        vehicle.setVehicleType(VehicleType.MOT);
        vehicle.setMotTestNumber("1234");

        when(motrSession.isAllowedOnTestExpiredPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicle);

        HashMap<String, Object> expectedContext = new HashMap<>();
        String headerOutput = "This vehicle’s MOT test expired on " + formatDate(testExpiryDate);
        expectedContext.put(HEADER_TEXT_KEY, headerOutput);
        expectedContext.put(VEHICLE_DESCRIPTIVE_TYPE_KEY, "vehicle");
        expectedContext.put(CONTENT_TEXT_ARRAY_KEY, TestExpiredResource.MOT_EXPIRED_CONTENT);
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(VRM,
                        VehicleType.MOT,
                        DataLayerMessageId.VEHICLE_MOT_TEST_EXPIRED,
                        DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                        getMessageText(headerOutput, TestExpiredResource.MOT_EXPIRED_CONTENT)));

        Response response = resource.testExpiredPageGet();

        assertEquals(200, response.getStatus());
        assertEquals("mot-test-expired", templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    @Test
    public void getWithNonMotVehicleDetailsWithoutMotTestNumberResultsInAnnualTestExpiredViewAndIsTestExpiredEqualsFalse()
            throws Exception {

        vehicle.setVehicleType(VehicleType.HGV);

        when(motrSession.isAllowedOnTestExpiredPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicle);

        HashMap<String, Object> expectedContext = new HashMap<>();
        String headerOutput = "First annual test was due " + formatDate(testExpiryDate);
        expectedContext.put(HEADER_TEXT_KEY, headerOutput);
        expectedContext.put(CONTENT_TEXT_ARRAY_KEY, getVehicleDescriptionType(VehicleType.HGV));
        expectedContext.put(VEHICLE_DESCRIPTIVE_TYPE_KEY, "vehicle");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(VRM,
                        VehicleType.HGV,
                        DataLayerMessageId.VEHICLE_ANNUAL_TEST_DUE,
                        DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                        getMessageText(headerOutput, getVehicleDescriptionType(VehicleType.HGV))));

        Response response = resource.testExpiredPageGet();

        assertEquals(200, response.getStatus());
        assertEquals("annual-test-expired", templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    @Test
    public void getWithNonMotVehicleDetailsWithMotTestNumberResultsInAnnualTestExpiredViewAndIsTestExpiredEqualsTrue() throws Exception {

        vehicle.setVehicleType(VehicleType.HGV);
        vehicle.setMotTestNumber("1234");

        when(motrSession.isAllowedOnTestExpiredPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicle);

        HashMap<String, Object> expectedContext = new HashMap<>();
        String headerOutput = "This vehicle’s annual test expired on " + formatDate(testExpiryDate);
        expectedContext.put(HEADER_TEXT_KEY, headerOutput);
        expectedContext.put(CONTENT_TEXT_ARRAY_KEY, getVehicleDescriptionType(VehicleType.HGV));
        expectedContext.put(VEHICLE_DESCRIPTIVE_TYPE_KEY, "vehicle");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(VRM,
                        VehicleType.HGV,
                        DataLayerMessageId.VEHICLE_ANNUAL_TEST_EXPIRED,
                        DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                        getMessageText(headerOutput, getVehicleDescriptionType(VehicleType.HGV))));

        Response response = resource.testExpiredPageGet();

        assertEquals(200, response.getStatus());
        assertEquals("annual-test-expired", templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    @Test
    public void getWithTrailerVehicleDetailsWithMotTestNumberResultsInAnnualTestExpiredViewAndIsTestExpiredEqualsTrue() throws Exception {

        vehicle.setVehicleType(VehicleType.TRAILER);
        vehicle.setMotTestNumber("1234");

        when(motrSession.isAllowedOnTestExpiredPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicle);

        HashMap<String, Object> expectedContext = new HashMap<>();
        String headerOutput = "This trailer’s annual test expired on " + formatDate(testExpiryDate);
        expectedContext.put(HEADER_TEXT_KEY, headerOutput);
        expectedContext.put(CONTENT_TEXT_ARRAY_KEY, getVehicleDescriptionType(VehicleType.TRAILER));
        expectedContext.put(VEHICLE_DESCRIPTIVE_TYPE_KEY, "trailer");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(VRM,
                        VehicleType.TRAILER,
                        DataLayerMessageId.TRAILER_ANNUAL_TEST_EXPIRED,
                        DataLayerMessageType.INELIGIBLE_FOR_REMINDER,
                        getMessageText(headerOutput, getVehicleDescriptionType(VehicleType.TRAILER))));

        Response response = resource.testExpiredPageGet();

        assertEquals(200, response.getStatus());
        assertEquals("annual-test-expired", templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    private String formatDate(LocalDate date) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TEST_EXPIRY_DATE_FORMAT);
        return date.format(dateTimeFormatter);
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

    private String getMessageText(String header, List<String> contentList) {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(header);
        contentList.forEach(stringJoiner::add);

        return stringJoiner.toString();
    }

    private List<String> getVehicleDescriptionType(VehicleType vehicleType) {
        List<String> list = new ArrayList<>(ANNUAL_EXPIRED_CONTENT);
        list.add(
                String.format(
                        "If the %s has been tested recently, it can take up to 10 working days for us to update our records",
                        getDescriptiveVehicleType(vehicleType)
            )
        );

        return list;
    }

    private String getDescriptiveVehicleType(VehicleType vehicleType) {
        if (VehicleType.isTrailer(vehicleType)) {
            return "trailer";
        }

        return "vehicle";
    }
}
