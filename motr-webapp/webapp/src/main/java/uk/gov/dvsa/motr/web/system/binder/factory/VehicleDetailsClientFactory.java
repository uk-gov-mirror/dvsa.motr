package uk.gov.dvsa.motr.web.system.binder.factory;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.FEATURE_TOGGLE_HGV_PSV_VEHICLES;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_BY_VRM_API_URI;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_CLIENT_CONNECTION_TIMEOUT;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_CLIENT_READ_TIMEOUT;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_URI;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN;

public class VehicleDetailsClientFactory implements BaseFactory<VehicleDetailsClient> {

    private final String readTimeout;
    private final String connectTimeout;
    private final String uri;
    private final String apiKey;

    @Inject
    public VehicleDetailsClientFactory(
            @SystemVariableParam(MOT_TEST_REMINDER_INFO_API_URI) String motOnlyUri,
            @SystemVariableParam(MOT_TEST_REMINDER_BY_VRM_API_URI) String uri,
            @SystemVariableParam(MOT_TEST_REMINDER_INFO_TOKEN) String apiKey,
            @SystemVariableParam(MOT_TEST_REMINDER_INFO_API_CLIENT_READ_TIMEOUT) String readTimeout,
            @SystemVariableParam(MOT_TEST_REMINDER_INFO_API_CLIENT_CONNECTION_TIMEOUT) String connectTimeout,
            @SystemVariableParam(FEATURE_TOGGLE_HGV_PSV_VEHICLES) String hgvPsvToggle
    ) {

        if (Boolean.parseBoolean(hgvPsvToggle)) {
            this.uri = uri;
        } else {
            this.uri = motOnlyUri;
        }

        this.apiKey = apiKey;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
    }

    @Override
    public VehicleDetailsClient provide() {

        int connectTimeoutInMs = Integer.parseInt(connectTimeout) * 1000;
        int readTimeoutInMs = Integer.parseInt(readTimeout) * 1000;

        return new VehicleDetailsClient(new ClientConfig()
                .property(ClientProperties.CONNECT_TIMEOUT, connectTimeoutInMs)
                .property(ClientProperties.READ_TIMEOUT, readTimeoutInMs), apiKey)
                .withByRegNumberUri(this.uri);
    }
}
