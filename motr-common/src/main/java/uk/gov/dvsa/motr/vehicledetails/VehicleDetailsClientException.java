package uk.gov.dvsa.motr.vehicledetails;

public class VehicleDetailsClientException extends Exception {

    public VehicleDetailsClientException(Exception exception) {

        super(exception);
    }

    public VehicleDetailsClientException(String message) {

        super(message);
    }
}
