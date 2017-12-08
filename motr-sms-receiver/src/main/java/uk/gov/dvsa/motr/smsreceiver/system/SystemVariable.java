package uk.gov.dvsa.motr.smsreceiver.system;

import uk.gov.dvsa.motr.smsreceiver.config.ConfigKey;

public enum  SystemVariable implements ConfigKey {

    LOG_LEVEL("LOG_LEVEL"),
    REGION("REGION");

    final String name;

    SystemVariable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
