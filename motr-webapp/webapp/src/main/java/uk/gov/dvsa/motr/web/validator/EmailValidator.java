package uk.gov.dvsa.motr.web.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements Validator {

    public static final String EMAIL_EMPTY_MESSAGE = "Enter your email address";
    public static final String EMAIL_INVALID_MESSAGE = "Enter a valid email address";
    public static final int MAX_LENGTH = 255;

    private String message;

    @Override
    public boolean isValid(String email) {

        if (email == null || email.isEmpty()) {
            this.message = EMAIL_EMPTY_MESSAGE;
            return false;
        }

        if (email.length() > MAX_LENGTH) {
            this.message = EMAIL_INVALID_MESSAGE;
            return false;
        }

        if (!isValidAccordingToGovNotify(email)) {
            this.message = EMAIL_INVALID_MESSAGE;
            return false;
        }

        return true;
    }

    public String getMessage() {

        return this.message;
    }

    /**
     * Java version of the email validator found here:
     * https://github.com/alphagov/notifications-utils/blob/master/notifications_utils/recipients.py
     */
    private boolean isValidAccordingToGovNotify(String email) {

        Pattern emailRegex = Pattern.compile("^[^\\s\";@]+@([^.@][^@]+)$");
        Pattern hostnamePart = Pattern.compile("^(xn-|[a-z0-9]+)(-[a-z0-9]+)*$", Pattern.CASE_INSENSITIVE);
        Pattern tldPart = Pattern.compile("^([a-z]{2,63}|xn--([a-z0-9]+-)*[a-z0-9]+)$", Pattern.CASE_INSENSITIVE);

        Matcher emailMatcher = emailRegex.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }

        String hostname = emailMatcher.group(1);

        if (hostname.contains("..")) {
            return false;
        }

        try {
            // Each part of the hostname must be < 64 chars.
            hostname = java.net.IDN.toASCII(hostname);
        } catch (IllegalArgumentException e) {
            return false;
        }

        String[] parts = hostname.split(Pattern.quote("."));
        int partsLength = parts.length;

        if (hostname.length() > 253 || partsLength < 2) {
            return false;
        }

        for (int i = 0; i < partsLength; i++) {
            String part = parts[i];
            if (part == null || part.isEmpty() || part.length() > 63 || !hostnamePart.matcher(part).matches()) {
                return false;
            }
        }

        if (!tldPart.matcher(parts[partsLength - 1]).matches()) {
            return false;
        }

        return true;
    }
}