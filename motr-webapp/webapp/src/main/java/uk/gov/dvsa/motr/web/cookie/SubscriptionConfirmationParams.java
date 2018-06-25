package uk.gov.dvsa.motr.web.cookie;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;

public class SubscriptionConfirmationParams extends SubscriptionParams {

    private VehicleType vehicleType;

    public VehicleType getVehicleType() {

        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {

        this.vehicleType = vehicleType;
    }

}
