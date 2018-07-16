package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.validator.EmailValidator;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class EmailResourceTest {

    private MotrSession motrSession;
    private TemplateEngineStub engine;
    private EmailResource resource;

    @Before
    public void setup() {

        motrSession = mock(MotrSession.class);
        engine = new TemplateEngineStub();
        SmartSurveyFeedback smartSurveyHelper = new SmartSurveyFeedback();
        resource = new EmailResource(motrSession, engine, spy(smartSurveyHelper));

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setVehicleType(VehicleType.MOT);
        vehicleDetails.setRegNumber("12345");
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);
        when(motrSession.getEmailFromSession()).thenReturn("test@test.com");
    }

    @Test
    public void emailTemplateIsRenderedOnGet() throws Exception {

        when(motrSession.isAllowedOnEmailPage()).thenReturn(true);
        assertEquals(200, resource.emailPage().getStatus());
        assertEquals("email", engine.getTemplate());

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("back_url", "vrm");
        expectedMap.put("continue_button_text", "Continue");
        expectedMap.put("back_button_text", "Back");
        expectedMap.put("email", "test@test.com");
        expectedMap.put("smartSurveyFeedback","http://www.smartsurvey.co.uk/s/MKVXI/?vrm=12345&contact_type=EMAIL&vehicle_type=MOT" +
                "&is_signing_before_first_mot_due=true");
        assertEquals(expectedMap.toString(), engine.getContext(Map.class).toString());
    }

    @Test
    public void onPostWithValid_ThenRedirectedToReviewPage() throws Exception {

        Response response = resource.emailPagePost("test@test.com");
        assertEquals(302, response.getStatus());
    }

    @Test
    public void onPostWithInvalidEmailFormatMessageWillBePassedToView() throws Exception {

        HashMap<String, String> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "email-input");
        expectedContext.put("message", EmailValidator.EMAIL_INVALID_MESSAGE);
        expectedContext.put("back_url", "vrm");
        expectedContext.put("email", "invalidEmail");
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("dataLayer", "{\"message-text\":\"Enter a valid email address\",\"message-type\":" +
                "\"USER_INPUT_ERROR\",\"message-id\":\"EMAIL_VALIDATION_ERROR\"}");
        expectedContext.put("smartSurveyFeedback","http://www.smartsurvey.co.uk/s/MKVXI/?vrm=12345&contact_type=EMAIL&vehicle_type=MOT" +
                "&is_signing_before_first_mot_due=true");

        Response response = resource.emailPagePost("invalidEmail");
        assertEquals(200, response.getStatus());
        assertEquals("email", engine.getTemplate());
        assertEquals(expectedContext, engine.getContext(Map.class));
    }

    @Test
    public void onPostWithEmptyEmailFormatMessageWillBePassedToView() throws Exception {

        HashMap<String, String> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "email-input");
        expectedContext.put("message", EmailValidator.EMAIL_EMPTY_MESSAGE);
        expectedContext.put("back_url", "vrm");
        expectedContext.put("email", "");
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("smartSurveyFeedback","http://www.smartsurvey.co.uk/s/MKVXI/?vrm=12345&contact_type=EMAIL&vehicle_type=MOT" +
                "&is_signing_before_first_mot_due=true");
        expectedContext.put("dataLayer", "{\"message-text\":\"Enter your email address\",\"message-type\":" +
                "\"USER_INPUT_ERROR\",\"message-id\":\"EMAIL_VALIDATION_ERROR\"}");

        Response response = resource.emailPagePost("");
        assertEquals(200, response.getStatus());
        assertEquals("email", engine.getTemplate());
        assertEquals(expectedContext, engine.getContext(Map.class));
    }
}
