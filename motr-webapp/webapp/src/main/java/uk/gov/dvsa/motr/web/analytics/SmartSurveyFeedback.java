package uk.gov.dvsa.motr.web.analytics;

public class SmartSurveyFeedback extends SmartSurveyHelper {

    private static final String SMART_SURVEY_FEEDBACK_URI = "http://www.smartsurvey.co.uk/s/MKVXI/";
    private static final String SMART_SURVEY_FEEDBACK_TEMPLATE_VARIABLE = "smartSurveyFeedback";

    public SmartSurveyFeedback() {

        super(SMART_SURVEY_FEEDBACK_TEMPLATE_VARIABLE, SMART_SURVEY_FEEDBACK_URI);
    }
}