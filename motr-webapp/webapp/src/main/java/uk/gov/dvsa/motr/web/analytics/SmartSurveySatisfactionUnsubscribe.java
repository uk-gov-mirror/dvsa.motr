package uk.gov.dvsa.motr.web.analytics;

public class SmartSurveySatisfactionUnsubscribe extends SmartSurveyHelper {

    private static final String SMART_SURVEY_SATISFACTION_URI = "http://www.smartsurvey.co.uk/s/CRUBR/";
    private static final String SMART_SURVEY_SATISFACTION_TEMPLATE_VARIABLE = "smartSurveySatisfaction";

    public SmartSurveySatisfactionUnsubscribe() {

        super(SMART_SURVEY_SATISFACTION_TEMPLATE_VARIABLE, SMART_SURVEY_SATISFACTION_URI);
    }
}