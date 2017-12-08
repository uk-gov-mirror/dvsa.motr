package uk.gov.dvsa.motr.notifier.component.subscription.persistence;

import java.time.LocalDate;

public class SubscriptionDbItem {

    private String id;

    private String vrm;

    private String email;

    private String motTestNumber;

    private LocalDate motDueDate;

    private String dvlaId;

    public SubscriptionDbItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getVrm() {
        return vrm;
    }

    public SubscriptionDbItem setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public SubscriptionDbItem setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getMotTestNumber() {
        return motTestNumber;
    }

    public SubscriptionDbItem setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
        return this;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public SubscriptionDbItem setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }

    public String getDvlaId() {

        return dvlaId;
    }

    public SubscriptionDbItem setDvlaId(String dvlaId) {

        this.dvlaId = dvlaId;
        return this;
    }
}
