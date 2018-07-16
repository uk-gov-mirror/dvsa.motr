package uk.gov.dvsa.motr.web.resource;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageId;
import uk.gov.dvsa.motr.web.analytics.DataLayerMessageType;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.validator.MotDueDateValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertNull;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VrmResourceTest {

    private static final String INVALID_REG_NUMBER = "________";
    private static final String VALID_REG_NUMBER = "FP12345";
    private static final String SYSTEM_ERROR_VRM = "ABC123";
    private static final String HONEY_POT = "";
    private static final String VEHICLE_NOT_FOUND = "We don't hold information about this vehicle.<br/><br/>" +
            "Check that you've typed in the correct registration number.";
    private static final String TRAILER_NOT_FOUND = "We don't hold information about this trailer.<br/><br/>" +
            "Check that you've typed in the correct trailer ID.";

    private VehicleDetailsClient client;
    private MotDueDateValidator motDueDateValidator;
    private TemplateEngineStub templateEngine;
    private VrmResource resource;
    private MotrSession motrSession;
    private SmartSurveyFeedback smartSurveyHelper;

    @Before
    public void setUp() {

        templateEngine = new TemplateEngineStub();
        client = mock(VehicleDetailsClient.class);
        motrSession = mock(MotrSession.class);
        motDueDateValidator = mock(MotDueDateValidator.class);
        smartSurveyHelper = new SmartSurveyFeedback();
        resource = new VrmResource(motrSession, templateEngine, client, motDueDateValidator, spy(smartSurveyHelper));
        when(motrSession.getVrmFromSession()).thenReturn("VRZ");
    }

    @Test
    public void getResultsInVrmTemplate() throws Exception {

        when(motrSession.getVrmFromSession()).thenReturn("VRZ5555");

        resource.vrmPageGet();
        assertEquals("vrm", templateEngine.getTemplate());
    }

    @Test
    public void postWithValidVrmResultsInRedirectToEmail() throws Exception {

        when(client.fetchByVrm(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(new VehicleDetails()));
        when(motDueDateValidator.isDueDateValid(any())).thenReturn(true);
        when(motDueDateValidator.isDueDateInTheFuture(any())).thenReturn(false);
        Response response = resource.vrmPagePost(VALID_REG_NUMBER, HONEY_POT);
        verify(motrSession, times(1)).setVehicleDetails(any(VehicleDetails.class));
        assertEquals(302, response.getStatus());
    }

    @Test
    public void postWithInvalidVrmResultsInVrmTemplateAndInlineErrorMessage() throws Exception {

        resource.vrmPagePost(INVALID_REG_NUMBER, HONEY_POT);

        assertEquals("vrm", templateEngine.getTemplate());

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "reg-number-input");
        expectedContext.put("message", VrmValidator.REGISTRATION_CAN_ONLY_CONTAIN_LETTERS_NUMBERS_AND_HYPHENS_MESSAGE);
        expectedContext.put("back_url", HomepageResource.HOMEPAGE_URL);
        expectedContext.put("vrm", INVALID_REG_NUMBER);
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("showInLine", "true");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("showSystemError", false);
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(INVALID_REG_NUMBER,
                        DataLayerMessageId.VRM_VALIDATION_ERROR,
                        DataLayerMessageType.USER_INPUT_ERROR,
                        VrmValidator.REGISTRATION_CAN_ONLY_CONTAIN_LETTERS_NUMBERS_AND_HYPHENS_MESSAGE));
        expectedContext.put("smartSurveyFeedback", "http://www.smartsurvey.co.uk/s/MKVXI/?vrm=" + INVALID_REG_NUMBER);

        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    @Test
    public void postWithValidVrmWhenVehicleServiceReturnsServiceErrorResultsInVrmTemplateAndSystemErrorMessage() throws Exception {

        doThrow(VehicleDetailsClientException.class).when(client).fetchByVrm(eq(SYSTEM_ERROR_VRM));
        resource.vrmPagePost(SYSTEM_ERROR_VRM, HONEY_POT);

        assertEquals("vrm", templateEngine.getTemplate());

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "reg-number-input");
        expectedContext.put("back_url", HomepageResource.HOMEPAGE_URL);
        expectedContext.put("vrm", SYSTEM_ERROR_VRM);
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("showInLine", "true");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("showSystemError", true);
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString("ABC123",
                        DataLayerMessageId.TRADE_API_CLIENT_EXCEPTION,
                        DataLayerMessageType.PUBLIC_API_REQUEST_ERROR,
                        "Something went wrong with the search. Try again later."));
        expectedContext.put("smartSurveyFeedback", "http://www.smartsurvey.co.uk/s/MKVXI/?vrm=" + SYSTEM_ERROR_VRM);

        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
    }

    @Test
    public void whenVehicleDetailsNotFoundShowErrorMessage() throws Exception {

        when(client.fetchByVrm(eq(VALID_REG_NUMBER))).thenReturn(Optional.empty());

        resource.vrmPagePost(VALID_REG_NUMBER, HONEY_POT);

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "reg-number-input");
        expectedContext.put("message", "We don't hold information about this vehicle.<br/>" +
                "<br/>Check that you've typed in the correct registration number.");
        expectedContext.put("back_url", HomepageResource.HOMEPAGE_URL);
        expectedContext.put("vrm", VALID_REG_NUMBER);
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("showSystemError", false);
        expectedContext.put("showInLine", "false");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString("FP12345",
                        DataLayerMessageId.VEHICLE_NOT_FOUND,
                        DataLayerMessageType.USER_INPUT_ERROR,
                        VEHICLE_NOT_FOUND));
        expectedContext.put("smartSurveyFeedback", "http://www.smartsurvey.co.uk/s/MKVXI/?vrm=" + VALID_REG_NUMBER);

        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
        assertEquals("vrm", templateEngine.getTemplate());
    }

    @Test
    public void whenSearchForTrailerButTrailerFunctionalityToggledOffShowErrorMessage() throws Exception {
        String trailerVrm = "A112131";

        when(motrSession.isTrailersFeatureToggleOn()).thenReturn(false);

        resource.vrmPagePost(trailerVrm, HONEY_POT);

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "reg-number-input");
        expectedContext.put("message", "We don't hold information about this vehicle.<br/>" +
                "<br/>Check that you've typed in the correct registration number.");
        expectedContext.put("back_url", HomepageResource.HOMEPAGE_URL);
        expectedContext.put("vrm", trailerVrm);
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("showSystemError", false);
        expectedContext.put("showInLine", "false");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(trailerVrm,
                        DataLayerMessageId.VEHICLE_NOT_FOUND,
                        DataLayerMessageType.USER_INPUT_ERROR,
                        VEHICLE_NOT_FOUND));
        expectedContext.put("smartSurveyFeedback", "http://www.smartsurvey.co.uk/s/MKVXI/?vrm=" + trailerVrm);

        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());

    }

    @Test
    public void whenVehicleDetailsForTrailerNotFoundShowErrorMessage() throws Exception {
        String trailerVrm = "A112131";

        when(motrSession.isTrailersFeatureToggleOn()).thenReturn(true);
        when(client.fetchByVrm(eq(trailerVrm))).thenReturn(Optional.empty());

        resource.vrmPagePost(trailerVrm, HONEY_POT);

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "reg-number-input");
        expectedContext.put("message", "We don't hold information about this trailer.<br/>" +
                "<br/>Check that you've typed in the correct trailer ID.");
        expectedContext.put("back_url", HomepageResource.HOMEPAGE_URL);
        expectedContext.put("vrm", trailerVrm);
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("showSystemError", false);
        expectedContext.put("showInLine", "false");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString(trailerVrm,
                        DataLayerMessageId.TRAILER_NOT_FOUND,
                        DataLayerMessageType.USER_INPUT_ERROR,
                        TRAILER_NOT_FOUND));
        expectedContext.put("smartSurveyFeedback", "http://www.smartsurvey.co.uk/s/MKVXI/?vrm=" + trailerVrm);

        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
        assertEquals("vrm", templateEngine.getTemplate());
    }

    @Test
    public void whenVehicleDetailsNotFoundHgvShowErrorMessage() throws Exception {

        when(client.fetchByVrm(eq(VALID_REG_NUMBER))).thenReturn(Optional.empty());
        when(motrSession.isHgvPsvVehiclesFeatureToggleOn()).thenReturn(true);

        resource.vrmPagePost(VALID_REG_NUMBER, HONEY_POT);

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("inputFieldId", "reg-number-input");
        expectedContext.put("message", "We don't hold information about this vehicle.<br/>" +
                "<br/>Check that you've typed in the correct registration number.");
        expectedContext.put("back_url", HomepageResource.HOMEPAGE_URL);
        expectedContext.put("vrm", VALID_REG_NUMBER);
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("showSystemError", false);
        expectedContext.put("showInLine", "false");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("dataLayer",
                getExpectedDataLayerJsonString("FP12345",
                        DataLayerMessageId.VEHICLE_NOT_FOUND,
                        DataLayerMessageType.USER_INPUT_ERROR,
                        VEHICLE_NOT_FOUND));
        expectedContext.put("smartSurveyFeedback", "http://www.smartsurvey.co.uk/s/MKVXI/?vrm=FP12345");

        assertEquals(expectedContext.toString(), templateEngine.getContext(Map.class).toString());
        assertEquals("vrm", templateEngine.getTemplate());
    }

    @Test
    public void whenVehicleDetailsFoundWithDueDateInThePast_thenShouldRedirect() throws Exception {
        LocalDate testExpiryDate = LocalDate.now().minusDays(2);

        VehicleDetails vehicle = new VehicleDetails();
        vehicle.setMotExpiryDate(testExpiryDate);
        vehicle.setVehicleType(VehicleType.MOT);

        when(client.fetchByVrm(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(vehicle));
        when(motDueDateValidator.isDueDateValid(any())).thenReturn(true);
        when(motDueDateValidator.isDueDateInTheFuture(any())).thenReturn(false);

        Response response = resource.vrmPagePost(VALID_REG_NUMBER, HONEY_POT);

        assertEquals(302, response.getStatus());
    }

    @Test
    public void whenHgvPsvExpiryDateIsUnknown_thenShouldRedirect() throws Exception {

        VehicleDetails vehicle = new VehicleDetails()
                .setMotExpiryDate(null)
                .setVehicleType(VehicleType.HGV);

        when(client.fetchByVrm(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(vehicle));

        Response response = resource.vrmPagePost(VALID_REG_NUMBER, HONEY_POT);

        // vehicleDetails are used by MotrSession.isAllowedOnUnknownTestDatePage()
        verify(motrSession, times(1)).setVehicleDetails(eq(vehicle));

        assertEquals(302, response.getStatus());
        assertEquals(URI.create(UnknownTestDueDateResource.UNKNOWN_TEST_DATE_PATH), response.getLocation());
    }

    @Test
    public void whenTrailerExpiryDateIsUnknown_thenShouldRedirect() throws Exception {
        String trailerVrm = "A112233";
        VehicleDetails vehicle = new VehicleDetails()
                .setMotExpiryDate(null)
                .setVehicleType(VehicleType.TRAILER);

        when(motrSession.isTrailersFeatureToggleOn()).thenReturn(true);
        when(client.fetchByVrm(eq(trailerVrm))).thenReturn(Optional.of(vehicle));

        Response response = resource.vrmPagePost(trailerVrm, HONEY_POT);

        // vehicleDetails are used by MotrSession.isAllowedOnUnknownTestDatePage()
        verify(motrSession, times(1)).setVehicleDetails(eq(vehicle));

        assertEquals(302, response.getStatus());
        assertEquals(URI.create(TrailerWithoutFirstAnnualTestResource.TRAILER_WITHOUT_FIRST_ANNUAL_TEST_PATH), response.getLocation());
    }

    @Test
    public void whenVisitingFromReviewPage_thenButtonAndLinkShowCorrectText() throws Exception {

        when(motrSession.visitingFromReviewPage()).thenReturn(true);
        when(client.fetchByVrm(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(new VehicleDetails()));
        when(motDueDateValidator.isDueDateValid(any())).thenReturn(true);
        when(motDueDateValidator.isDueDateInTheFuture(any())).thenReturn(true);

        resource = new VrmResource(motrSession, templateEngine, client, motDueDateValidator, spy(smartSurveyHelper));
        Response response = resource.vrmPagePost(VALID_REG_NUMBER, HONEY_POT);

        assertEquals("review", response.getHeaders().get("Location").get(0).toString());
        assertEquals(302, response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    public void whenRetrievingPageWithExistingRegNumber_thenTheRegIsAddedToThePageModel() throws Exception {

        when(motrSession.visitingFromReviewPage()).thenReturn(true);
        resource.vrmPageGet();

        Map context = templateEngine.getContext(Map.class);
        assertEquals("VRZ", context.get("vrm"));
    }

    @Test
    public void whenHoneyPotValueIsNotEmpty_thenRedirectToEmailConfirmationPendingPage() throws Exception {

        when(motrSession.visitingFromReviewPage()).thenReturn(true);
        when(client.fetchByVrm(eq(VALID_REG_NUMBER))).thenReturn(Optional.of(new VehicleDetails()));
        when(motDueDateValidator.isDueDateValid(any())).thenReturn(true);
        when(motDueDateValidator.isDueDateInTheFuture(any())).thenReturn(true);

        resource = new VrmResource(motrSession, templateEngine, client, motDueDateValidator, spy(smartSurveyHelper));
        Response response = resource.vrmPagePost(VALID_REG_NUMBER, "Honey");

        assertEquals("email-confirmation-pending", response.getHeaders().get("Location").get(0).toString());
        assertEquals(302, response.getStatus());
    }

    private String getExpectedDataLayerJsonString(String vrm,
                                                  DataLayerMessageId messageId,
                                                  DataLayerMessageType messageType,
                                                  String messageText) {

        JSONObject content = new JSONObject();
        content.put("vrm", vrm);
        if (messageId != null) {
            content.put("message-id", messageId);
            content.put("message-type", messageType);
            content.put("message-text", messageText);
        }

        return content.toString();
    }
}
