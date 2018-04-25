package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;

public class SubscriptionCriteria {
    private final String name;
    private final LocalDate testDueDate;
    private final VehicleType vehicleType;

    public SubscriptionCriteria(String name, LocalDate testDueDate, VehicleType vehicleType) {
        this.name = name;
        this.testDueDate = testDueDate;
        this.vehicleType = vehicleType;
    }

    public String getName() {
        return name;
    }

    public LocalDate getTestDueDate() {
        return testDueDate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}
