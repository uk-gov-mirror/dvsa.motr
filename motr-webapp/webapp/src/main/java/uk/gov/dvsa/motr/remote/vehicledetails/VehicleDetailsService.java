package uk.gov.dvsa.motr.remote.vehicledetails;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.vehicle.VehicleDetailsExceptionEvent;

import java.util.Optional;

public class VehicleDetailsService {

    public static VehicleDetails getVehicleDetails(String vrm, VehicleDetailsClient client) {

        try {
            Optional<VehicleDetails> vehicle = client.fetch(vrm);
            return vehicle.get();
        } catch (VehicleDetailsClientException exception) {
            EventLogger.logErrorEvent(new VehicleDetailsExceptionEvent().setVrm(vrm), exception);
            return null;
        }
    }
}
