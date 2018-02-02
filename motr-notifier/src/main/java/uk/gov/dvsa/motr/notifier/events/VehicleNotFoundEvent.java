package uk.gov.dvsa.motr.notifier.events;

import java.time.LocalDate;

public class VehicleNotFoundEvent extends SubscriptionProcessedEvent {

    public VehicleNotFoundEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public VehicleNotFoundEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public VehicleNotFoundEvent setExpiryDate(LocalDate expiryDate) {

        params.put("expiry-date", expiryDate.toString());
        return this;
    }

    public VehicleNotFoundEvent setMotTestNumber(String motTestNumber) {
        params.put("mot-test-number", motTestNumber);
        return this;
    }

    public VehicleNotFoundEvent setDvlaId(String dvlaId) {
        params.put("dvla-id", dvlaId);
        return this;
    }

    @Override
    public String getCode() {

        return "VEHICLE-NOT-FOUND";
    }
}
