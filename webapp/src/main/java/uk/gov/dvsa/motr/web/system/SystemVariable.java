package uk.gov.dvsa.motr.web.system;

import uk.gov.dvsa.motr.web.config.ConfigKey;

public enum SystemVariable implements ConfigKey {

    LOG_LEVEL("LOG_LEVEL"),
    REGION("REGION"),
    GOV_NOTIFY_URI("GOV_NOTIFY_URI"),
    // must be secret
    GOV_NOTIFY_API_TOKEN("GOV_NOTIFY_API_TOKEN"),
    MOT_TEST_REMINDER_INFO_API_URI("MOT_TEST_REMINDER_INFO_API_URI"),
    CONFIRMATION_TEMPLATE_ID("CONFIRMATION_TEMPLATE_ID"),
    // must be secret
    MOT_TEST_REMINDER_INFO_API_TOKEN("MOT_TEST_REMINDER_INFO_API_TOKEN"),
    STATIC_ASSETS_HASH("STATIC_ASSETS_HASH"),
    STATIC_ASSETS_URL("STATIC_ASSETS_URL"),
    DB_TABLE_PENDING_SUBSCRIPTION("DB_TABLE_PENDING_SUBSCRIPTION"),
    DB_TABLE_SUBSCRIPTION("DB_TABLE_SUBSCRIPTION"),
    BASE_URL("BASE_URL"),
    DO_WARM_UP("WARM_UP"),
    WARM_UP_TIMEOUT_SEC("WARM_UP_TIMEOUT_SEC");

    final String name;

    SystemVariable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
