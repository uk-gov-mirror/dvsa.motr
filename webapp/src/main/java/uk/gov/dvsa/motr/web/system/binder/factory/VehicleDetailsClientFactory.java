package uk.gov.dvsa.motr.web.system.binder.factory;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.config.Config;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_URI;

public class VehicleDetailsClientFactory implements BaseFactory<VehicleDetailsClient> {

    private Config config;

    @Inject
    public VehicleDetailsClientFactory(Config config) {

        this.config = config;
    }

    @Override
    public VehicleDetailsClient provide() {

        String uri = this.config.getValue(MOT_TEST_REMINDER_INFO_API_URI);
        String apiKey = ""; // TODO this.config.getValue(MOT_TEST_REMINDER_INFO_API_TOKEN);
        ClientConfig config = new ClientConfig()
                .connectorProvider(new ApacheConnectorProvider());

        return new VehicleDetailsClient(config, uri, apiKey);
    }
}
