package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VrmResourceTest {

    private static final String INVALID_REG_NUMBER = "________";
    private static final String VALID_REG_NUMBER = "FP12345";
    private static final String TEST_BASE_URL = "http://some-test/url";

    private VehicleDetailsClient client;
    private TemplateEngineStub templateEngine;
    private VrmResource resource;
    private MotrSession motrSession;

    @Before
    public void setUp() {

        templateEngine = new TemplateEngineStub();
        client = mock(VehicleDetailsClient.class);
        motrSession = mock(MotrSession.class);
        resource = new VrmResource("", motrSession, templateEngine, client);
        when(motrSession.getRegNumberFromSession()).thenReturn("VRZ");
    }

    @Test
    public void getResultsInVrmTemplate() throws Exception {

        when(motrSession.getRegNumberFromSession()).thenReturn("VRZ5555");

        resource.vrmPageGet();
        assertEquals("vrm", templateEngine.getTemplate());
    }

    @Test
    public void postWithValidVrmResultsInRedirectToEmail() throws Exception {

        when(client.fetch(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(new VehicleDetails()));
        Response response = resource.vrmPagePost(VALID_REG_NUMBER);
        assertEquals(303, response.getStatus());
    }

    @Test
    public void postWithInvalidVrmResultsInVrmTemplateAndInlineErrorMessage() throws Exception {

        resource.vrmPagePost(INVALID_REG_NUMBER);

        assertEquals("vrm", templateEngine.getTemplate());
        Map context = templateEngine.getContext(Map.class);
        assertNotNull(context.get("message"));
        assertTrue((Boolean) context.get("showInLine"));
    }

    @Test
    public void whenVehicleDetailsNotFoundShowErrorMessage() throws Exception {

        when(client.fetch(eq(VALID_REG_NUMBER))).thenReturn(Optional.empty());

        resource.vrmPagePost(VALID_REG_NUMBER);

        assertEquals("vrm", templateEngine.getTemplate());
        Map context = templateEngine.getContext(Map.class);
        assertEquals("Check that you’ve typed in the correct registration number.<br/>" +
                "<br/>You can only sign up if your vehicle has had its first MOT.", context.get("message"));
    }

    @Test
    public void whenVisitingFromReviewPage_thenButtonAndLinkShowCorrectText() throws Exception {

        when(motrSession.visitingFromReviewPage()).thenReturn(true);
        when(client.fetch(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(new VehicleDetails()));

        resource = new VrmResource(TEST_BASE_URL, motrSession, templateEngine, client);
        Response response = resource.vrmPagePost(VALID_REG_NUMBER);

        assertEquals(TEST_BASE_URL + "/review", response.getHeaders().get("Location").get(0).toString());
        assertEquals(303, response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    public void whenRetrievingPageWithExistingRegNumber_thenTheRegIsAddedToThePageModel() throws Exception {

        when(motrSession.visitingFromReviewPage()).thenReturn(true);
        resource.vrmPageGet();

        Map context = templateEngine.getContext(Map.class);
        assertEquals("review", context.get("back_location"));
        assertEquals("VRZ", context.get("vrm"));
    }
}
