package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.IS_TEST_EXPIRED_KEY;
import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.TEST_EXPIRY_DATE_FORMAT;
import static uk.gov.dvsa.motr.web.resource.TestExpiredResource.TEST_EXPIRY_DATE_KEY;

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
        expectedContext.put(IS_TEST_EXPIRED_KEY, false);
        expectedContext.put(TEST_EXPIRY_DATE_KEY, formatDate(testExpiryDate));
        expectedContext.put("dataLayer", "{\"vrm\":\"" + VRM + "\"}");

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
        expectedContext.put(IS_TEST_EXPIRED_KEY, true);
        expectedContext.put(TEST_EXPIRY_DATE_KEY, formatDate(testExpiryDate));
        expectedContext.put("dataLayer", "{\"vrm\":\"" + VRM + "\"}");

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
        expectedContext.put(IS_TEST_EXPIRED_KEY, false);
        expectedContext.put(TEST_EXPIRY_DATE_KEY, formatDate(testExpiryDate));
        expectedContext.put("dataLayer", "{\"vrm\":\"" + VRM + "\"}");

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
        expectedContext.put(IS_TEST_EXPIRED_KEY, true);
        expectedContext.put(TEST_EXPIRY_DATE_KEY, formatDate(testExpiryDate));
        expectedContext.put("dataLayer", "{\"vrm\":\"" + VRM + "\"}");

        Response response = resource.testExpiredPageGet();

        assertEquals(200, response.getStatus());
        assertEquals("annual-test-expired", templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    private String formatDate(LocalDate date) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TEST_EXPIRY_DATE_FORMAT);
        return date.format(dateTimeFormatter);
    }
}
