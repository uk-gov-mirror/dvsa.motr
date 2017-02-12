package uk.gov.dvsa.motr.subscriptionloader;

import uk.gov.dvsa.motr.config.ConfigKey;

public enum SystemVariable implements ConfigKey {

    LOG_LEVEL("LOG_LEVEL"),
    REGION("REGION"),
    TABLE_NAME("DB_TABLE_SUBSCRIPTION"),
    QUEUE_URL("SUBSCRIPTIONS_QUEUE_URL"),
    INFLIGHT_BATCHES("INFLIGHT_BATCHES");


    String value;

    SystemVariable(String name) {
        this.value = name;
    }

    public String getName() {
        return value;
    }

}
