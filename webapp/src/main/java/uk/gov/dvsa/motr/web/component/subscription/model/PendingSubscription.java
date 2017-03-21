package uk.gov.dvsa.motr.web.component.subscription.model;

import java.time.LocalDate;

public class PendingSubscription {

    private String confirmationId;

    private String vrm;

    private String email;

    private LocalDate motDueDate;

    public String getConfirmationId() {
        return confirmationId;
    }

    public PendingSubscription setConfirmationId(String id) {
        this.confirmationId = id;
        return this;
    }


    public String getVrm() {
        return vrm;
    }

    public PendingSubscription setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PendingSubscription setEmail(String email) {
        this.email = email;
        return this;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public PendingSubscription setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }
}
