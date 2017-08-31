package uk.gov.dvsa.motr.web.validator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionsValidationService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class PhoneNumberValidatorTest {

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

        assertFalse(validator.isValid(PHONE_NUMBER_LANDLINE));
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
    }
}
