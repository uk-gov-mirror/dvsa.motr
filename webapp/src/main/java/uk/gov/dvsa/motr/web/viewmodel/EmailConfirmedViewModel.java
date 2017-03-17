package uk.gov.dvsa.motr.web.viewmodel;

import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;

import java.time.LocalDate;

public class EmailConfirmedViewModel {

    private static String UNKNOWN_STRING = "Unknown";
    private String registration;
    private String email;
    private LocalDate expiryDate;

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

        return DateDisplayHelper.asDisplayDate(expiryDate);
    }

    public EmailConfirmedViewModel setExpiryDate(LocalDate expiryDate) {

        this.expiryDate = expiryDate;
        return this;
    }
}
