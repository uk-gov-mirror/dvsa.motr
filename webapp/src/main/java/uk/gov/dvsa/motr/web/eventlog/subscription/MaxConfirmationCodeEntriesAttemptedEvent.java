package uk.gov.dvsa.motr.web.eventlog.subscription;


import uk.gov.dvsa.motr.eventlog.Event;

public class MaxConfirmationCodeEntriesAttemptedEvent extends Event {

    @Override
    public String getCode() {

        return "MAX-SMS-CONFIRMATION-CODE-ENTRIES";
    }

    public MaxConfirmationCodeEntriesAttemptedEvent setConfirmationId(String confirmationId) {

        params.put("confirmation-id", confirmationId);
        return this;
    }

    public MaxConfirmationCodeEntriesAttemptedEvent setPhoneNumber(String phoneNumber) {

        params.put("phone-number", phoneNumber);
        return this;
    }
}
