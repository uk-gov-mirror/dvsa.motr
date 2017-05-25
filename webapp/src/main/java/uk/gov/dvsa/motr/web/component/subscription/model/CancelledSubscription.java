package uk.gov.dvsa.motr.web.component.subscription.model;

public class CancelledSubscription {
    private String unsubscribeId;

    private String vrm;

    private String email;

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public CancelledSubscription setUnsubscribeId(String id) {
        this.unsubscribeId = id;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public CancelledSubscription setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CancelledSubscription setEmail(String email) {
        this.email = email;
        return this;
    }
}
