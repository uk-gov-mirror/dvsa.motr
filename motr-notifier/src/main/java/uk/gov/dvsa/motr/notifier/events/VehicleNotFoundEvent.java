package uk.gov.dvsa.motr.notifier.events;

public class VehicleNotFoundEvent extends SubscriptionProcessedEvent {

    @Override
    public String getCode() {

        return "VEHICLE-NOT-FOUND";
    }
}
