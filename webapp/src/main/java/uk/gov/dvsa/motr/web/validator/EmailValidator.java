package uk.gov.dvsa.motr.web.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailValidator {

    public static final String EMAIL_EMPTY_MESSAGE = "Enter your email address";
    public static final String EMAIL_INVALID_MESSAGE = "Enter a valid email address";
    public static final int MAX_LENGTH = 255;

    private String message;

    public boolean isValid(String email) {

        if (email == null || email.isEmpty()) {
            this.message = EMAIL_EMPTY_MESSAGE;
            return false;
        }

        if (email.length() > MAX_LENGTH) {
            this.message = EMAIL_INVALID_MESSAGE;
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
