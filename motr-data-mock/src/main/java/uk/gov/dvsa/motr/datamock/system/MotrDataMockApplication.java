package uk.gov.dvsa.motr.datamock.system;

import org.glassfish.jersey.server.ResourceConfig;


public class MotrDataMockApplication extends ResourceConfig {

    public MotrDataMockApplication() {
        packages("uk.gov.dvsa.motr.datamock");
    }
}