package uk.gov.dvsa.motr.web.component.subscription.model;

import java.time.LocalDate;

public class Subscription {

    private String unsubscribeId;

    private String vrm;

    private String email;

    private LocalDate motDueDate;

    private String motTestNumber;

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public Subscription setUnsubscribeId(String id) {
        this.unsubscribeId = id;
        return this;
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

    public String getMotTestNumber() {
        return this.motTestNumber;
    }

    public Subscription setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
        return this;
    }
}
