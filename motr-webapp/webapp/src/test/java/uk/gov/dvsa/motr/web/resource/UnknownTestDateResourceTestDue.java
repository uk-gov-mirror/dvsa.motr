package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.resource.HomepageResource.HOMEPAGE_URL;
import static uk.gov.dvsa.motr.web.resource.UnknownTestDueDateResource.UNKNOWN_TEST_DATE_TEMPLATE;

public class UnknownTestDateResourceTestDue {

    private static final String VRM = "PSV-UNKNEXP";

    private TemplateEngineStub templateEngine;
    private UnknownTestDueDateResource resource;
    private MotrSession motrSession;
    private VehicleDetails vehicle;

    @Before
    public void setUp() {

        templateEngine = new TemplateEngineStub();
        motrSession = mock(MotrSession.class);
        resource = new UnknownTestDueDateResource(motrSession, templateEngine);
        vehicle = new VehicleDetails();
        vehicle.setVehicleType(VehicleType.PSV);

        when(motrSession.getVrmFromSession()).thenReturn(VRM);
    }

    @Test
    public void getWhenNotAllowedOnPage_ResultsInRedirectToHomePage() {

        when(motrSession.isAllowedOnUnknownTestDatePage()).thenReturn(false);

        Response response = resource.testExpiryUnknownPageGet();

        verify(motrSession, times(0)).getVehicleDetailsFromSession();
        assertEquals(302, response.getStatus());
    }

    @Test
    public void getIsSuccessful_whenUserIsAllowedToAccessIt() {

        when(motrSession.isAllowedOnUnknownTestDatePage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicle);

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("back_url", HOMEPAGE_URL);
        expectedContext.put("dataLayer", "{\"vrm\":\"" + VRM +
                "\",\"vehicle-data-origin\":\"PSV\",\"message-text\":\"We don't know when this vehicle's first annual test is due\"," +
                "\"message-type\":\"INELIGIBLE_FOR_REMINDER\",\"message-id\":\"ANNUAL_TEST_DATE_UNKNOWN\"}");

        Response response = resource.testExpiryUnknownPageGet();

        assertEquals(200, response.getStatus());
        assertEquals(UNKNOWN_TEST_DATE_TEMPLATE, templateEngine.getTemplate());
        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

}
