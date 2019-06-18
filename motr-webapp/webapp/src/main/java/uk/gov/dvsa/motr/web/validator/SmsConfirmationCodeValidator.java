package uk.gov.dvsa.motr.web.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsConfirmationCodeValidator implements FieldValidator {

    private static final Pattern CONF_CODE_VALIDATION_REGEX = Pattern.compile("\\d{5}");

    public static final String EMPTY_CONFIRMATION_CODE_MESSAGE = "Enter 5-digit code from text message<br/>" +
            "<br/>It can take a couple of minutes for the text to arrive.";
    public static final String EMPTY_CONFIRMATION_CODE_MESSAGE_AT_FIELD = "Enter 5 digits from text message";
    public static final String INVALID_CONFIRMATION_CODE_MESSAGE = "Entered code is invalid<br/>" +
            "<br/>Enter 5 digits you received in text message";
    public static final String INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD = "Enter 5 digits from text message";

    public static final String CODE_INCORRECT_3_TIMES = "You can’t subscribe now. <br/>" +
            "Code was entered incorrectly 3 times. <br/>" +
            "<br/>Come back later and try to subscribe again.";

    public static final String CODE_ALREADY_RESENT = "Activation code was already resent. <br/>" +
            "It can take several minutes to arrive. <br/>" +
            "<br/>If you didn’t receive the code, come back later and try to subscribe again.";

    private String message;
    private String messageAtField;

    @Override
    public String getMessage() {

        return message;
    }

    @Override
    public void setMessage(String message) {

        this.message = message;
    }

    @Override
    public String getMessageAtField() {

        return messageAtField;
    }

    @Override
    public void setMessageAtField(String messageAtField) {

        this.messageAtField = messageAtField;
    }

    @Override
    public boolean isValid(String confirmationCode) {

        if (confirmationCode == null || confirmationCode.isEmpty()) {
            message = EMPTY_CONFIRMATION_CODE_MESSAGE;
            messageAtField = EMPTY_CONFIRMATION_CODE_MESSAGE_AT_FIELD;

            return false;
        }

        return validate(confirmationCode);
    }

    private boolean validate(String confirmationCode) {

        Matcher matcher = CONF_CODE_VALIDATION_REGEX.matcher(confirmationCode);

        if (!matcher.matches()) {
            message = INVALID_CONFIRMATION_CODE_MESSAGE;
            messageAtField = INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD;

            return false;
        }

        return true;
    }
}
