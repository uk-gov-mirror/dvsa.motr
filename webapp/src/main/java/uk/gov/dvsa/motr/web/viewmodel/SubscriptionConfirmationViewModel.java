package uk.gov.dvsa.motr.web.viewmodel;

import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;

import java.time.LocalDate;

public class SubscriptionConfirmationViewModel {

    private String vrm;
    private LocalDate expiryDate;
    private String email;

    public String getVrm() {

        return vrm;
    }

    public SubscriptionConfirmationViewModel setVrm(String vrm) {

        this.vrm = vrm;
        return this;
    }

    public String getExpiryDate() {

        return DateDisplayHelper.asDisplayDate(expiryDate);
    }

    public SubscriptionConfirmationViewModel setExpiryDate(LocalDate expiryDate) {

        this.expiryDate = expiryDate;
        return this;
    }

    public String getEmail() {

        return email;
    }

    public SubscriptionConfirmationViewModel setEmail(String email) {

        this.email = email;
        return this;
    }
}
