package uk.gov.dvsa.motr.web.analytics;

public class SmartSurveySatisfactionSubscribe extends SmartSurveyHelper {

    private static final String SMART_SURVEY_SATISFACTION_URI = "http://www.smartsurvey.co.uk/s/YN642/";
    private static final String SMART_SURVEY_SATISFACTION_TEMPLATE_VARIABLE = "smartSurveySatisfaction";

    public SmartSurveySatisfactionSubscribe() {

        super(SMART_SURVEY_SATISFACTION_TEMPLATE_VARIABLE, SMART_SURVEY_SATISFACTION_URI);
    }
}