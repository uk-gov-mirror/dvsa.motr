package uk.gov.dvsa.motr.smsreceiver.system;

import org.glassfish.jersey.server.ResourceConfig;

import uk.gov.dvsa.motr.smsreceiver.system.binder.ConfigBinder;

public class MotrSmsApplication extends ResourceConfig {

    public MotrSmsApplication() {

        packages("uk.gov.dvsa.motr.smsreceiver");

        register(new ConfigBinder());
    }
}
