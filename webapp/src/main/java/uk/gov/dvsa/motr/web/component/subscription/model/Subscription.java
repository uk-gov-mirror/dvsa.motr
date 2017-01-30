package uk.gov.dvsa.motr.web.component.subscription.model;

import java.time.LocalDate;

public class Subscription {

    private String id;

    private String vrm;

    private String email;

    private LocalDate motDueDate;

    public Subscription(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getVrm() {
        return vrm;
    }

    public Subscription setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Subscription setEmail(String email) {
        this.email = email;
        return this;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public Subscription setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }
}
