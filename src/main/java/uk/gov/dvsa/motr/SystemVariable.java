package uk.gov.dvsa.motr;

import uk.gov.dvsa.motr.config.ConfigKey;

public enum SystemVariable implements ConfigKey {

    LOG_LEVEL("LOG_LEVEL"),
    REGION("REGION"),
    DB_TABLE_SUBSCRIPTION("DB_TABLE_SUBSCRIPTION"),
    DB_TABLE_CANCELLED_SUBSCRIPTION("DB_TABLE_CANCELLED_SUBSCRIPTION"),
    GOV_NOTIFY_API_TOKEN("GOV_NOTIFY_API_TOKEN"),
    GOV_NOTIFY_STATUS_REPORT_EMAIL_TEMPLATE("GOV_NOTIFY_STATUS_REPORT_EMAIL_TEMPLATE"),
    STATUS_EMAIL_RECIPIENTS("STATUS_EMAIL_RECIPIENTS");

    String value;

    SystemVariable(String name) {

        this.value = name;
    }

    public String getName() {

        return value;
    }
}
