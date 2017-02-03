package uk.gov.dvsa.motr.web.remote.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.Provider;

@Provider
public class VehicleDetailsResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(VehicleDetailsResponseHandler.class);

    public Vehicle getVehicleDetailsFromResponse(Response response, String vrm)
            throws VehicleNotFoundException {

        int responseStatus = response.getStatus();
        if (responseStatus == 404) {
            logger.error("Vehicle not found: {}", vrm);
            throw new VehicleNotFoundException("Vehicle with vrm " + vrm + "not found");
        }

        Family responseFamily = response.getStatusInfo().getFamily();

        if (responseFamily == Family.SERVER_ERROR) {
            logger.error("Response of {} returned for vehicle with: {}", responseStatus, vrm);
            throw new ServerErrorException("Server error thrown when searching for vrm " + vrm, responseStatus);
        }

        if (responseFamily == Family.CLIENT_ERROR) {
            logger.error("Response of {} returned for vehicle with: {}", responseStatus, vrm);
            throw new ClientErrorException("Client error thrown when searching for vrm " + vrm, responseStatus);
        }

        return response.readEntity(Vehicle.class);
    }
}
