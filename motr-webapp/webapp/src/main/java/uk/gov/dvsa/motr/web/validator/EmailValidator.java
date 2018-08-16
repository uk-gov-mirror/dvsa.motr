package uk.gov.dvsa.motr.web.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements Validator {

    private static final int MAX_LENGTH = 255;

    private static final String VALID_LOCAL_CHARS = "a-zA-Z0-9.!#$%&'*+/=?^_`{|}~\\-";
    private static final String EMAIL_REGEX = String.format("^[%s]+@([^.@][^@\\s]+)$", VALID_LOCAL_CHARS);

    private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern HOSTNAME_PART_PATTERN = Pattern.compile("^(xn-|[a-z0-9]+)(-[a-z0-9]+)*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TLD_PART_PATTERN = Pattern.compile("^([a-z]{2,63}|xn--([a-z0-9]+-)*[a-z0-9]+)$", Pattern.CASE_INSENSITIVE);

    public static final String EMAIL_EMPTY_MESSAGE = "Enter your email address";
    public static final String EMAIL_INVALID_MESSAGE = "Enter a valid email address";

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
     * Java version of the email validator found here: https://github
     * .com/alphagov/notifications-utils/blob/master/notifications_utils/recipients.py
     */
    private boolean isValidAccordingToGovNotify(String email) {

        Matcher emailMatcher = EMAIL_REGEX_PATTERN.matcher(email);
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

        // Use a limit of -1 to include trailing empty strings.
        String[] hostnameParts = hostname.split(Pattern.quote("."), -1);

        if (hostname.length() > 253 || hostnameParts.length < 2) {
            return false;
        }

        for (String part : hostnameParts) {
            if (StringUtils.isBlank(part) || part.length() > 63 || !HOSTNAME_PART_PATTERN.matcher(part).matches()) {
                return false;
            }
        }

        return TLD_PART_PATTERN.matcher(hostnameParts[hostnameParts.length - 1]).matches();
    }
}