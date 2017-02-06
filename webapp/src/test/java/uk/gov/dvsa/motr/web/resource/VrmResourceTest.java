package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VrmResourceTest {

    private static final String INVALID_REG_NUMBER = "________";
    private static final String VALID_REG_NUMBER = "FP12345";

    private VehicleDetailsClient client;
    private TemplateEngineStub templateEngine;
    private VrmResource resource;

    @Before
    public void setUp() {

        templateEngine = new TemplateEngineStub();
        client = mock(VehicleDetailsClient.class);
        resource = new VrmResource(templateEngine, client);
    }

    @Test
    public void getResultsInVrmTemplate() throws Exception {

        resource.vrmPageGet();

        assertEquals("vrm", templateEngine.getTemplate());
    }

    @Test
    public void postWithValidVrmResultsInVrmTemplate() throws Exception {

        when(client.fetch(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(new VehicleDetails()));
        resource.vrmPagePost(VALID_REG_NUMBER);

        assertEquals("vrm", templateEngine.getTemplate());
        assertEquals(VALID_REG_NUMBER, templateEngine.getContext(Map.class).get("vrm"));
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
}
