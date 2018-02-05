package uk.gov.dvsa.motr.notifier.events;

import java.time.LocalDate;

public class UpdateMotExpiryDateFailedEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "UPDATING-MOT-EXPIRY-DATE-ERROR";
    }

    public UpdateMotExpiryDateFailedEvent setNewExpiryDate(LocalDate newExpiryDate) {

        params.put("new-expiry-date", newExpiryDate.toString());
        return this;
    }
}
