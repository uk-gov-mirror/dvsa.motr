package uk.gov.dvsa.motr.web.system.binder.factory;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_URI;

public class VehicleDetailsClientFactory implements BaseFactory<VehicleDetailsClient> {

    private String uri;

    @Inject
    public VehicleDetailsClientFactory(@SystemVariableParam(MOT_TEST_REMINDER_INFO_API_URI) String uri) {

        this.uri = uri;
    }

    @Override
    public VehicleDetailsClient provide() {

        String apiKey = ""; // TODO this.config.getValue(MOT_TEST_REMINDER_INFO_API_TOKEN);
        ClientConfig config = new ClientConfig()
                .connectorProvider(new ApacheConnectorProvider());

        return new VehicleDetailsClient(config, this.uri, apiKey);
    }
}
