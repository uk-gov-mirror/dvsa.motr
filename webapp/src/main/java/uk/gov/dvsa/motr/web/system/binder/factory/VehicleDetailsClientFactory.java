package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.remote.client.VehicleDetailsClient;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_URI;

public class VehicleDetailsClientFactory implements BaseFactory<VehicleDetailsClient> {

    private Config config;
    private Client client;

    @Inject
    public VehicleDetailsClientFactory(Config config) {

        this.config = config;
        this.client = ClientBuilder.newClient();
    }

    @Override
    public VehicleDetailsClient provide() {

        String vehicleDetailsProviderEndpoint = this.config.getValue(MOT_TEST_REMINDER_INFO_API_URI);
        return new VehicleDetailsClient(this.client.target(vehicleDetailsProviderEndpoint));
    }
}
