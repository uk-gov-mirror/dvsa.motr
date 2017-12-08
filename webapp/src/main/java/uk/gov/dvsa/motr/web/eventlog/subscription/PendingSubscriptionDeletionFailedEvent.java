package uk.gov.dvsa.motr.web.eventlog.subscription;


import uk.gov.dvsa.motr.eventlog.Event;

public class PendingSubscriptionDeletionFailedEvent extends Event {

    @Override
    public String getCode() {

        return "PENDING-SUBSCRIPTION-DELETION-FAILED";
    }

    public PendingSubscriptionDeletionFailedEvent setConfirmationId(String confirmationId) {

        params.put("confirmation-id", confirmationId);
        return this;
    }

    public PendingSubscriptionDeletionFailedEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public PendingSubscriptionDeletionFailedEvent setContact(String contact) {

        params.put("contact", contact);
        return this;
    }

    public PendingSubscriptionDeletionFailedEvent setMessage(String message) {

        params.put("message", message);
        return this;
    }

    public PendingSubscriptionDeletionFailedEvent setErrorMessage(String errorMessage) {

        params.put("error-message", errorMessage);
        return this;
    }
}
