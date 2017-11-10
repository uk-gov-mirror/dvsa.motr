package uk.gov.dvsa.motr.web.eventlog.subscription;


import uk.gov.dvsa.motr.eventlog.Event;

public class PendingSubscriptionAlreadyDeletedEvent extends Event {

    @Override
    public String getCode() {

        return "PENDING-SUBSCRIPTION-ALREADY-DELETED";
    }

    public PendingSubscriptionAlreadyDeletedEvent setConfirmationId(String confirmationId) {

        params.put("confirmation-id", confirmationId);
        return this;
    }

    public PendingSubscriptionAlreadyDeletedEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public PendingSubscriptionAlreadyDeletedEvent setContact(String contact) {

        params.put("contact", contact);
        return this;
    }

    public PendingSubscriptionAlreadyDeletedEvent setMessage(String message) {

        params.put("message", message);
        return this;
    }

    public PendingSubscriptionAlreadyDeletedEvent setErrorMessage(String errorMessage) {

        params.put("error-message", errorMessage);
        return this;
    }
}
