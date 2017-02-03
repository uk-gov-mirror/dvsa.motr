package uk.gov.dvsa.motr.web.remote.client;

import javax.inject.Singleton;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

@Singleton
public class VehicleDetailsClient {

    private static final String REG_NUMBER = "vrm";

    private WebTarget target;

    public VehicleDetailsClient(WebTarget target) {

        this.target = target;
    }

    public Response retrieveVehicleDetails(String vrm) throws VehicleNotFoundException {

        return this.target.queryParam(REG_NUMBER, vrm).request().get();
    }
}
