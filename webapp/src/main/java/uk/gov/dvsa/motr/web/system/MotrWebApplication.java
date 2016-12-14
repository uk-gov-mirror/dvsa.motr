package uk.gov.dvsa.motr.web.system;

import org.glassfish.jersey.server.ResourceConfig;

import uk.gov.dvsa.motr.web.system.binder.ConfigBinder;
import uk.gov.dvsa.motr.web.system.binder.TemplateEngineBinder;

public class MotrWebApplication extends ResourceConfig {

    public MotrWebApplication() {

        packages("uk.gov.dvsa.motr.web");

        register(new ConfigBinder());
        register(new TemplateEngineBinder());
    }
}
