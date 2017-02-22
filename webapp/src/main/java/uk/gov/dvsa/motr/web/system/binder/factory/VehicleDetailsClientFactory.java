package uk.gov.dvsa.motr.web.system.binder.factory;

import org.glassfish.jersey.client.ClientConfig;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_URI;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN;

public class VehicleDetailsClientFactory implements BaseFactory<VehicleDetailsClient> {

    private String uri;
    private String apiKey;

    @Inject
    public VehicleDetailsClientFactory(
            @SystemVariableParam(MOT_TEST_REMINDER_INFO_API_URI) String uri,
            @SystemVariableParam(MOT_TEST_REMINDER_INFO_TOKEN) String apiKey
    ) {

        this.uri = uri;
        this.apiKey = apiKey;
    }

    @Override
    public VehicleDetailsClient provide() {

        return new VehicleDetailsClient(new ClientConfig(), this.uri, apiKey);
    }
}
