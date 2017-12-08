package uk.gov.dvsa.motr.web.analytics;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SmartSurveyHelperTests {

    private SmartSurveyHelper smartSurveyHelper;

    @Before
    public void setUp() {

        smartSurveyHelper = new SmartSurveyHelper("http://www.test.com/", "testTemplateVariable");
    }

    @Test
    public void smartSurveyAttributesFormattedInCorrectFormat() {

        smartSurveyHelper.putAttribute("key1", "value1");
        smartSurveyHelper.putAttribute("key2", "value2");

        Map<String, String> map = smartSurveyHelper.formatAttributes();

        assertTrue(map.containsKey("testTemplateVariable"));
        assertEquals("http://www.test.com/?key1=value1&key2=value2", map.get("testTemplateVariable"));
    }

    @Test
    public void smartSurveyHelperWithNoAttributesReturnsEmptyMap() {

        Map<String, String> map = smartSurveyHelper.formatAttributes();
        assertTrue(map.isEmpty());
    }

    @Test
    public void smartSurveyHelperThatHasBeenClearedReturnsEmptyMap() {

        smartSurveyHelper.putAttribute("key1", "value1");
        smartSurveyHelper.clear();

        Map<String, String> map = smartSurveyHelper.formatAttributes();
        assertTrue(map.isEmpty());
    }
}
