package uk.gov.dvsa.motr.web.viewmodel;

public class EmailConfirmedViewModel {

    private static final String UNKNOWN_STRING = "Unknown";
    private String registration;
    private String email;
    private String expiryDate;

    public String getRegistration() {
        return registration;
    }

    public EmailConfirmedViewModel setRegistration(String registration) {

        this.registration = registration;
        return this;
    }

    public String getEmail() {

        return email;
    }

    public EmailConfirmedViewModel setEmail(String email) {

        this.email = email;
        return this;
    }

    public String getExpiryDate() {

        if (expiryDate == null) {
            return UNKNOWN_STRING;
        }

        return expiryDate;
    }

    public EmailConfirmedViewModel setExpiryDate(String expiryDate) {

        this.expiryDate = expiryDate;
        return this;
    }
}
