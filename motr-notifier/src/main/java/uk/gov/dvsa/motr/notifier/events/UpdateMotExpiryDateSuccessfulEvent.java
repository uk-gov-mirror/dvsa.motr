package uk.gov.dvsa.motr.notifier.events;

import java.time.LocalDate;

public class UpdateMotExpiryDateSuccessfulEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "SUCCESSFULLY-UPDATED-MOT-EXPIRY-DATE";
    }

    public UpdateMotExpiryDateSuccessfulEvent setNewExpiryDate(LocalDate newExpiryDate) {

        params.put("new-expiry-date", newExpiryDate.toString());
        return this;
    }
}
