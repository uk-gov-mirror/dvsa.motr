package uk.gov.dvsa.motr.smsreceiver.system;

import uk.gov.dvsa.motr.config.ConfigKey;

public enum SystemVariable implements ConfigKey {

    LOG_LEVEL("LOG_LEVEL"),
    REGION("REGION"),
    DB_TABLE_SUBSCRIPTION("DB_TABLE_SUBSCRIPTION"),
    DB_TABLE_CANCELLED_SUBSCRIPTION("DB_TABLE_CANCELLED_SUBSCRIPTION"),
    NOTIFY_BEARER_TOKEN("NOTIFY_BEARER_TOKEN"),
    GOV_NOTIFY_API_TOKEN("GOV_NOTIFY_API_TOKEN"),
    SMS_UNSUBSCRIPTION_CONFIRMATION_TEMPLATE_ID("SMS_UNSUBSCRIPTION_CONFIRMATION_TEMPLATE_ID"),
    GA_TRACING_ID("GA_TRACING_ID");

    final String name;

    SystemVariable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
