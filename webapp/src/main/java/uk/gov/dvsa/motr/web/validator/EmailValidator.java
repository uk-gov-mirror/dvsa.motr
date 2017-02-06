package uk.gov.dvsa.motr.web.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailValidator {

    public static final String EMAIL_EMPTY_MESSAGE = "Enter your email address";
    public static final String EMAIL_INVALID_MESSAGE = "Enter a valid email address";

    private String message;
    private String email;

    public EmailValidator(String email) {
        this.email = email;
    }

    public boolean isValid() {

        if (this.email == null || this.email.isEmpty()) {
            this.message = EMAIL_EMPTY_MESSAGE;
            return false;
        }

        try {
            new InternetAddress(email, true).validate();
        } catch (AddressException e) {
            this.message = EMAIL_INVALID_MESSAGE;
            return false;
        }

        return true;
    }

    public String getMessage() {
        return this.message;
    }
}
