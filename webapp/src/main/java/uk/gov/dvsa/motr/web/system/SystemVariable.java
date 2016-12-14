package uk.gov.dvsa.motr.web.system;

import uk.gov.dvsa.motr.web.config.ConfigKey;

public enum SystemVariable implements ConfigKey {

    LOG_LEVEL("LOG_LEVEL"),
    REGION("REGION"),
    DB_CONNECTION_STRING("DB_CONNECTION_STRING"),
    GOV_NOTIFY_URI("GOV_NOTIFY_URI"),
    // must be secret
    GOV_NOTIFY_API_TOKEN("GOV_NOTIFY_API_TOKEN"),
    MOT_TEST_REMINDER_INFO_API_URI("MOT_TEST_REMINDER_INFO_API_URI"),
    // must be secret
    MOT_TEST_REMINDER_INFO_API_TOKEN("MOT_TEST_REMINDER_INFO_API_TOKEN"),
    STATIC_ASSETS_HASH("STATIC_ASSETS_HASH"),
    STATIC_ASSETS_URL("STATIC_ASSETS_URL");

    final String name;

    SystemVariable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
