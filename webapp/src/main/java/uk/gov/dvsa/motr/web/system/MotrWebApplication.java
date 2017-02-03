package uk.gov.dvsa.motr.web.system;

import org.glassfish.jersey.server.ResourceConfig;

import uk.gov.dvsa.motr.web.system.binder.ConfigBinder;
import uk.gov.dvsa.motr.web.system.binder.TemplateEngineBinder;
import uk.gov.dvsa.motr.web.system.binder.VehicleServicesBinder;

public class MotrWebApplication extends ResourceConfig {

    public MotrWebApplication() {

        packages("uk.gov.dvsa.motr.web");

        register(new VehicleServicesBinder());
        register(new ConfigBinder());
        register(new TemplateEngineBinder());
    }
}
