package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChannelSelectionResourceTest {

    private MotrSession motrSession;
    private TemplateEngineStub engine;
    private ChannelSelectionResource resource;

    @Before
    public void setup() {

        motrSession = mock(MotrSession.class);
        engine = new TemplateEngineStub();
        SmartSurveyFeedback smartSurveyHelper = new SmartSurveyFeedback();
        resource = new ChannelSelectionResource(motrSession, engine, smartSurveyHelper);
    }

    @Test
    public void channelSelectionTemplateIsRenderedOnGet() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setVehicleType(VehicleType.MOT);
        vehicleDetails.setRegNumber("12345");
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);
        when(motrSession.getChannelFromSession()).thenReturn("text");
        when(motrSession.isAllowedOnChannelSelectionPage()).thenReturn(true);
        assertEquals(200, resource.channelSelectionPageGet().getStatus());
        assertEquals("channel-selection", engine.getTemplate());
    }

    @Test
    public void onPostWithValid_ThenRedirectedToPhoneNumberEntryPage() throws Exception {

        when(motrSession.getChannelFromSession()).thenReturn("text");
        Response response = resource.channelSelectionPagePost("text");

        assertEquals(302, response.getStatus());
    }

    @Test
    public void onPostWithValid_ThenRedirectedToEmailEntryPage() throws Exception {

        when(motrSession.getChannelFromSession()).thenReturn("email");
        Response response = resource.channelSelectionPagePost("email");

        assertEquals(302, response.getStatus());
    }

}
