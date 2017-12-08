package uk.gov.dvsa.motr.notifier.events;

import java.time.LocalDate;

public class VehicleDetailsRetrievalFailedEvent extends SubscriptionProcessedEvent {

    public VehicleDetailsRetrievalFailedEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public VehicleDetailsRetrievalFailedEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public VehicleDetailsRetrievalFailedEvent setExpiryDate(LocalDate expiryDate) {

        params.put("expiry-date", expiryDate.toString());
        return this;
    }

    @Override
    public String getCode() {

        return "VEHICLE-DETAILS-RETRIEVAL-FAILED";
    }
}
