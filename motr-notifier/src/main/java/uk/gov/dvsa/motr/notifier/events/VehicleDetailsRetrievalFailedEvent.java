package uk.gov.dvsa.motr.notifier.events;

public class VehicleDetailsRetrievalFailedEvent extends SubscriptionProcessedEvent {

    @Override
    public String getCode() {

        return "VEHICLE-DETAILS-RETRIEVAL-FAILED";
    }
}
