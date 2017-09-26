package uk.gov.dvsa.motr.web.validator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionsValidationService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class PhoneNumberValidatorTest {

    private static final String EMPTY_PHONE_NUMBER_MESSAGE = "Enter your mobile number";
    private static final String INVALID_PHONE_NUMBER_MESSAGE_HEADING = "The number you entered is not a UK mobile phone " +
            "number";
    private static final String INVALID_PHONE_NUMBER_MESSAGE_FIELD = "Enter a valid UK mobile phone number";
    private static final String TOO_MANY_SUBSCRIPTIONS = "You can’t subscribe right now. You have already subscribed to two" +
            " MOT reminders for this phone number <br/>" +
            "<br/> You may unsubscribe from one of the reminders or use a different mobile phone number.";
    private static final String TOO_MANY_SUBSCRIPTIONS_AT_FIELD = "Use a different mobile phone number";

    private static final String PHONE_NUMBER_TOO_SHORT = "0780652671";
    private static final String PHONE_NUMBER_TOO_LONG = "078065267111";
    private static final String PHONE_NUMBER_LANDLINE = "02890435617";
    private static final String PHONE_NUMBER_VALID = "07809716253";
    private static final String PHONE_NUMBER_NON_NUMERIC = "078AA716EE3";
    private SubscriptionsValidationService subscriptionsValidationService;
    private PhoneNumberValidator validator;

    @Before
    public void setUp() {

        subscriptionsValidationService = mock(SubscriptionsValidationService.class);

        this.validator = new PhoneNumberValidator(subscriptionsValidationService);
    }

    @Test
    @UseDataProvider("invalidPhoneNumbers")
    public void testThatPhoneNumberIsValidated(String invalidNumber) {
        assertFalse(validator.isValid(invalidNumber));
    }

    @DataProvider
    public static Object[][] invalidPhoneNumbers() {

        return new Object[][] {
                {""}, {null}, {PHONE_NUMBER_TOO_SHORT}, {PHONE_NUMBER_TOO_LONG}, {PHONE_NUMBER_NON_NUMERIC}
        };
    }

    @Test
    public void landLineIsInvalid() {

        when(subscriptionsValidationService.hasMaxTwoSubscriptionsForPhoneNumber(any())).thenReturn(true);

        assertFalse(validator.isValid(PHONE_NUMBER_LANDLINE));
        assertEquals(INVALID_PHONE_NUMBER_MESSAGE_FIELD, validator.getMessageAtField());
        assertEquals(INVALID_PHONE_NUMBER_MESSAGE_HEADING, validator.getMessage());
    }

    @Test
    public void validPhoneNumberIsValid() {

        when(subscriptionsValidationService.hasMaxTwoSubscriptionsForPhoneNumber(PHONE_NUMBER_VALID)).thenReturn(true);

        assertTrue(validator.isValid(PHONE_NUMBER_VALID));
    }

    @Test
    public void phoneNumberWithTwoSubscriptionsIsInvalid() {

        when(subscriptionsValidationService.hasMaxTwoSubscriptionsForPhoneNumber(PHONE_NUMBER_VALID)).thenReturn(false);

        assertFalse(validator.isValid(PHONE_NUMBER_VALID));
        assertEquals(TOO_MANY_SUBSCRIPTIONS_AT_FIELD, validator.getMessageAtField());
        assertEquals(TOO_MANY_SUBSCRIPTIONS, validator.getMessage());
    }
}
