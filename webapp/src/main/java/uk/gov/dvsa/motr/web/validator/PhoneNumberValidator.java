package uk.gov.dvsa.motr.web.validator;

import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionsValidationService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class PhoneNumberValidator {

    private static final String EMPTY_PHONE_NUMBER_MESSAGE = "Enter your mobile number";
    private static final String INVALID_PHONE_NUMBER_MESSAGE_HEADING = "The number you entered is not a UK mobile phone " +
            "number";
    private static final String INVALID_PHONE_NUMBER_MESSAGE_FIELD = "Enter a valid UK mobile phone number";
    private static final String TOO_MANY_SUBSCRIPTIONS = "You can’t subscribe right now. You have already subscribed to two" +
            " MOT reminders for this phone number <br/>" +
            "<br/> You may unsubscribe from one of the reminders or use a different mobile phone number.";
    private static final String TOO_MANY_SUBSCRIPTIONS_AT_FIELD = "Use a different mobile phone number";

    private String message;
    private String messageAtField;

    private final SubscriptionsValidationService subscriptionsValidationService;

    @Inject
    public PhoneNumberValidator(SubscriptionsValidationService subscriptionsValidationService) {

        this.subscriptionsValidationService = subscriptionsValidationService;
    }

    public boolean isValid(String phoneNumber) {

        return hasPhoneNumberBeenEntered(phoneNumber) && isPhoneNumberAValidUkNumber(phoneNumber)
                && hasPhoneNumberGotMaxTwoSubscriptions(phoneNumber);
    }

    private boolean hasPhoneNumberBeenEntered(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            message = EMPTY_PHONE_NUMBER_MESSAGE;
            messageAtField = EMPTY_PHONE_NUMBER_MESSAGE;

            return false;
        }

        return true;
    }

    private boolean isPhoneNumberAValidUkNumber(String phoneNumber) {

        Pattern validationRegex = Pattern.compile(
                "^(\\(?07\\d{3}\\)?|" +
                "\\+447\\d{3}|447\\d{3}|" +
                "00447\\d{3}|\\(44\\)7\\d{3}|" +
                "\\(\\+44\\)7\\d{3})\\d{3}\\d{3}$"
        );

        Matcher matcher = validationRegex.matcher(phoneNumber);

        if (!matcher.matches()) {
            message = INVALID_PHONE_NUMBER_MESSAGE_HEADING;
            messageAtField = INVALID_PHONE_NUMBER_MESSAGE_FIELD;

            return false;
        }

        return true;
    }

    private boolean hasPhoneNumberGotMaxTwoSubscriptions(String phoneNumber) {

        if (!subscriptionsValidationService.hasMaxTwoSubscriptionsForPhoneNumber(phoneNumber)) {
            message = TOO_MANY_SUBSCRIPTIONS;
            messageAtField = TOO_MANY_SUBSCRIPTIONS_AT_FIELD;

            return false;
        }

        return true;
    }

    public String getMessage() {

        return message;
    }

    public String getMessageAtField() {

        return messageAtField;
    }
}
