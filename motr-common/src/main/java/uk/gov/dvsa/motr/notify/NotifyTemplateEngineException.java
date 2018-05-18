package uk.gov.dvsa.motr.notify;

import java.util.Map;

public class NotifyTemplateEngineException extends Exception {

    private final String templateString;
    private final Map<String, String> templateParameters;

    public NotifyTemplateEngineException() {
        super();
        this.templateString = null;
        this.templateParameters = null;
    }

    public NotifyTemplateEngineException(String message) {
        super(message);
        this.templateString = null;
        this.templateParameters = null;
    }

    public NotifyTemplateEngineException(String message, String templateString, Map<String, String> params) {
        super(message);

        this.templateString = templateString;
        this.templateParameters = params;
    }

    public String getTemplateString() {
        return this.templateString;
    }

    public Map<String, String> getTemplateParameters() {
        return this.templateParameters;
    }
}
