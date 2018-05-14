package uk.gov.dvsa.motr.notify;

import uk.gov.dvsa.motr.eventlog.Event;

public class NotifyTemplateEngineFailedEvent extends Event {

    public enum Type {
        ERROR_LOADING_TEMPLATE,
        ERROR_GETTING_PARAMETERS
    }

    public NotifyTemplateEngineFailedEvent setType(Type type) {

        params.put("type", type.toString());
        return this;
    }

    @Override
    public String getCode() {

        return "NOTIFY-TEMPLATE-ENGINE-FAILED";
    }
}
