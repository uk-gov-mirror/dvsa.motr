package uk.gov.dvsa.motr.web.remote.client;

public class VehicleNotFoundException extends Exception {

    public VehicleNotFoundException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
