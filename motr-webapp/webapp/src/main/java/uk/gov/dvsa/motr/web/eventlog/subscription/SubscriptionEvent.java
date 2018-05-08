package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class SubscriptionEvent extends Event {

    public SubscriptionEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public SubscriptionEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public SubscriptionEvent setVehicleType(VehicleType vehicleType) {

        params.put("vehicle-type", vehicleType.name());
        return this;
    }

    public SubscriptionEvent setDueDate(LocalDate motDueDate) {

        params.put("mot-due-date", motDueDate.format(DateTimeFormatter.ISO_DATE));
        return this;
    }

    public SubscriptionEvent setReasonForCancellation(String reasonForCancellation) {
        params.put("reason_for_cancellation", reasonForCancellation);
        return this;
    }
}
