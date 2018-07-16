package uk.gov.dvsa.motr.web.analytics;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SmartSurveyFeedbackTests {

    private SmartSurveyFeedback smartSurveyHelper;

    @Before
    public void setUp() {

        smartSurveyHelper = new SmartSurveyFeedback();
    }

    @Test
    public void smartSurveyAttributesFormattedInCorrectFormat() {

        smartSurveyHelper.addVrm("value1");
        smartSurveyHelper.addVehicleType(VehicleType.MOT);
        smartSurveyHelper.addContactType("value3");
        smartSurveyHelper.addIsSigningBeforeFirstMotDue(true);

        Map<String, String> map = smartSurveyHelper.formatAttributes();

        assertTrue(map.containsKey("smartSurveyFeedback"));
        assertEquals("http://www.smartsurvey.co.uk/s/MKVXI/?vrm=value1&contact_type=value3&vehicle_type=MOT" +
                        "&is_signing_before_first_mot_due=true", map.get("smartSurveyFeedback"));
    }

    @Test
    public void smartSurveyHelperWithNoAttributesReturnsEmptyMap() {

        Map<String, String> map = smartSurveyHelper.formatAttributes();
        assertTrue(map.isEmpty());
    }

    @Test
    public void smartSurveyHelperThatHasBeenClearedReturnsEmptyMap() {

        smartSurveyHelper.addVrm("value1");
        smartSurveyHelper.clear();

        Map<String, String> map = smartSurveyHelper.formatAttributes();
        assertTrue(map.isEmpty());
    }
}
