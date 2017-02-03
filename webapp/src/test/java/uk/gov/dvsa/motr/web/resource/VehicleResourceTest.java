package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.service.VehicleService;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class VehicleResourceTest {

    private static final VehicleService VEHICLE_SERVICE = mock(VehicleService.class);
    private static final TemplateEngine TEMPLATE_ENGINE = mock(TemplateEngine.class);
    private static final String INVALID_REG_NUMBER = "FNZ6110";
    private static final String TEST_MESSAGE = "Test message";
    private static final String VALID_REG_NUMBER = "FP12345";
    public static final String VEHICLE_DETAILS_COOKIE_KEY = "vehicleDetailsCookie";

    @Test
    public void vehicleDetailsTemplateIsRenderedWithEmptyMapWhenNoCookiePresent() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        VehicleDetailsResource resource = new VehicleDetailsResource(engine, VEHICLE_SERVICE);

        assertEquals(RESPONSE, resource.vehicleDetailsPageGetRequest(""));
        assertEquals("vehicle-details", engine.getTemplate());
        assertEquals(mockedValidTemplateMap(""), engine.getContext(Map.class));
    }

    @Test
    public void vehicleDetailsTemplateIsRenderedWithVehicleDetailsMapFromCookieWhenValuePresent() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        VehicleDetailsResource resource = new VehicleDetailsResource(engine, VEHICLE_SERVICE);

        assertEquals(RESPONSE, resource.vehicleDetailsPageGetRequest(INVALID_REG_NUMBER));
        assertEquals("vehicle-details", engine.getTemplate());
        assertEquals(mockedValidTemplateMap(INVALID_REG_NUMBER), engine.getContext(Map.class));
    }

    @Test
    public void whenPostCalled_thenCorrectVrmPassedToVehicleService_AndExpectedMapSentInResponse() throws Exception {

        Map<String, Object> testPostMap = mockedPostTemplateMap(VALID_REG_NUMBER, TEST_MESSAGE, false);
        when(VEHICLE_SERVICE.createVehicleResponseMap(VALID_REG_NUMBER)).thenReturn(testPostMap);

        VehicleDetailsResource resource = new VehicleDetailsResource(TEMPLATE_ENGINE, VEHICLE_SERVICE);
        Response response = resource.vehicleDetailsPagePostRequest(VALID_REG_NUMBER);

        assertEquals(VALID_REG_NUMBER, response.getCookies().get(VEHICLE_DETAILS_COOKIE_KEY).getValue());
        assertEquals(200, response.getStatus());
        verify(TEMPLATE_ENGINE, times(1)).render("vehicle-details", testPostMap);
    }

    private Map<String, String> mockedValidTemplateMap(String vehicleDetailsValue) {

        Map<String, String> vehicleDetailsMap = new HashMap<>();
        vehicleDetailsMap.put("vehicleDetails", vehicleDetailsValue);
        return vehicleDetailsMap;
    }

    private Map<String, Object> mockedPostTemplateMap(
            String vehicleDetailsValue,
            String message,
            boolean showInLine
    ) {

        Map<String, Object> vehicleDetailsMap = new HashMap<>();
        vehicleDetailsMap.put("vehicleDetails", vehicleDetailsValue);
        vehicleDetailsMap.put("message", message);
        vehicleDetailsMap.put("showInLine", showInLine);
        return vehicleDetailsMap;
    }
}
