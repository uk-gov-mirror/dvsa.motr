package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.util.Objects;

public class SubscriptionCriteria {
    private final LocalDate testDueDate;
    private final VehicleType vehicleType;

    public SubscriptionCriteria(LocalDate testDueDate, VehicleType vehicleType) {
        this.testDueDate = testDueDate;
        this.vehicleType = vehicleType;
    }

    public LocalDate getTestDueDate() {
        return testDueDate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubscriptionCriteria that = (SubscriptionCriteria) o;
        return Objects.equals(testDueDate, that.testDueDate)
                && vehicleType == that.vehicleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testDueDate, vehicleType);
    }

    @Override
    public String toString() {
        return "SubscriptionCriteria{" +
                "testDueDate=" + testDueDate +
                ", vehicleType=" + vehicleType +
                '}';
    }
}
