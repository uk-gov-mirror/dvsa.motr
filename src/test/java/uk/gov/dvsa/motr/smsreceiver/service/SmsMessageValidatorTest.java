package uk.gov.dvsa.motr.smsreceiver.service;

import org.junit.Test;

import uk.gov.dvsa.motr.smsreceiver.model.Message;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SmsMessageValidatorTest {

    private static final String VALID_MESSAGE_WITH_VRM = "STOP SOMETHING";
    private static final String MOBILE_NUMBER = "1234567";
    private static final String INVALID_MESSAGE = "STOA SOMETHING";
    SmsMessageValidator smsMessageValidator = new SmsMessageValidator();

    @Test
    public void messageContainsEnoughDetails_TheReturnsTrue() {

        Message message = new Message();
        message.setMessage(VALID_MESSAGE_WITH_VRM);
        message.setSubscribersMobileNumber(MOBILE_NUMBER);

        boolean valid = smsMessageValidator.messageHasSufficientDetails(message);
        assertTrue(valid);
    }

    @Test
    public void messageContainsNoStop_TheReturnsFalse() {

        Message message = new Message();
        message.setMessage(INVALID_MESSAGE);
        message.setSubscribersMobileNumber(MOBILE_NUMBER);

        boolean valid = smsMessageValidator.messageHasSufficientDetails(message);
        assertFalse(valid);
    }

    @Test
    public void messageContainsMobile_TheReturnsFalse() {

        Message message = new Message();
        message.setMessage(VALID_MESSAGE_WITH_VRM);
        message.setSubscribersMobileNumber("");

        boolean valid = smsMessageValidator.messageHasSufficientDetails(message);
        assertFalse(valid);
    }
}
