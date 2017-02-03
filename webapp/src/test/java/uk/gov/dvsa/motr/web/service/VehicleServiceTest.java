package uk.gov.dvsa.motr.web.service;

import org.junit.Test;

import uk.gov.dvsa.motr.web.remote.client.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.remote.client.VehicleDetailsResponseHandler;
import uk.gov.dvsa.motr.web.remote.client.VehicleNotFoundException;
import uk.gov.dvsa.motr.web.validator.VrmValidator;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VehicleServiceTest {

    private static final String TEST_REG = "test reg";
    private static final String TESTREG_FORMATTED = "TESTREG";
    private static final String INVALID_MESSAGE = "some not-valid message";
    private static final String NOT_FOUND_MESSAGE = "Check that you’ve typed in the correct registration number" +
            ".<br/><br/>You can only sign up if your vehicle has had its first MOT.";

    private VrmValidator vehicleDetailsValidatorMock = mock(VrmValidator.class);
    private VehicleDetailsClient vehicleDetailsClientMock = mock(VehicleDetailsClient.class);
    private VehicleDetailsResponseHandler vehicleDetailsResponseHandlerMock = mock(VehicleDetailsResponseHandler.class);
    private Response responseMock = mock(Response.class);

    @Test
    public void whenInvalidVrm_ThenReturnsAppropriateMessageInMap() {

        when(vehicleDetailsValidatorMock.isValid(TEST_REG)).thenReturn(false);
        when(vehicleDetailsValidatorMock.getMessage()).thenReturn(INVALID_MESSAGE);
        when(vehicleDetailsValidatorMock.shouldShowInLineMessage()).thenReturn(true);

        VehicleService vehicleService = new VehicleService(vehicleDetailsValidatorMock, vehicleDetailsClientMock,
                vehicleDetailsResponseHandlerMock);

        Map<String, Object> returnedMap = vehicleService.createVehicleResponseMap(TEST_REG);
        Map<String, Object> expectedVehicleDetailsMap = createExpectedMap(INVALID_MESSAGE, TESTREG_FORMATTED, true);

        assertEquals(expectedVehicleDetailsMap, returnedMap);
    }

    @Test
    public void whenValidVrmAndValidResponse_ThenReturnsAppropriateResponseObject() throws VehicleNotFoundException {

        when(vehicleDetailsValidatorMock.isValid(any())).thenReturn(true);
        when(vehicleDetailsClientMock.retrieveVehicleDetails(TESTREG_FORMATTED)).thenReturn(responseMock);

        VehicleService vehicleService = new VehicleService(vehicleDetailsValidatorMock, vehicleDetailsClientMock,
                vehicleDetailsResponseHandlerMock);

        vehicleService.createVehicleResponseMap(TEST_REG);

        verify(vehicleDetailsResponseHandlerMock, times(1)).getVehicleDetailsFromResponse(eq(responseMock), eq(TESTREG_FORMATTED));
    }

    @Test
    public void whenANon200ResponseIsReceived_ThenCorrectExceptionThrownAndCorrectMapBuilt() throws VehicleNotFoundException {

        when(vehicleDetailsValidatorMock.isValid(any())).thenReturn(true);
        when(vehicleDetailsClientMock.retrieveVehicleDetails(TESTREG_FORMATTED)).thenReturn(responseMock);
        when(vehicleDetailsResponseHandlerMock.getVehicleDetailsFromResponse(responseMock, TESTREG_FORMATTED)).thenThrow(
                VehicleNotFoundException.class);

        VehicleService vehicleService = new VehicleService(vehicleDetailsValidatorMock, vehicleDetailsClientMock,
                vehicleDetailsResponseHandlerMock);

        Map<String, Object> returnedMap = vehicleService.createVehicleResponseMap(TEST_REG);
        Map<String, Object> expectedVehicleDetailsMap = createExpectedMap(NOT_FOUND_MESSAGE, TESTREG_FORMATTED, false);

        assertEquals(expectedVehicleDetailsMap, returnedMap);
    }

    private Map<String, Object> createExpectedMap(String expectedMessage, String vehicleDetails, boolean showInline) {
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("message", expectedMessage);
        expectedMap.put("showInLine", showInline);
        expectedMap.put("vehicleDetails", vehicleDetails);
        return expectedMap;
    }
}
