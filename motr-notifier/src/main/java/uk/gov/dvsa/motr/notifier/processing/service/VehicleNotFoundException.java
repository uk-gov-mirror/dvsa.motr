package uk.gov.dvsa.motr.notifier.processing.service;

public class VehicleNotFoundException extends Exception {

    public VehicleNotFoundException(String message) {

        super(message);
    }
}
