package uk.gov.dvsa.motr.web.validator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class VrmValidatorTest {

    @Test
    public void isValidThrowsErrorMessageWhenNoRegistrationProvided() {

        VrmValidator vrmValidator = new VrmValidator();

        assertFalse(vrmValidator.isValid(null));
        assertEquals("Enter your vehicle’s registration", vrmValidator.getMessage());
    }

    @Test
    @UseDataProvider("invalidCharactersForRegistration")
    public void isValidThrowsErrorMessageWhenRegistrationContainsInvalidCharacters(String invalidRegistration) {

        VrmValidator vrmValidator = new VrmValidator();

        assertFalse(vrmValidator.isValid(invalidRegistration));
        assertEquals("Registration can only contain letters, numbers and hyphens", vrmValidator.getMessage());
    }

    @Test
    public void isValidThrowsErrorMessageWhenRegistrationIsTooLong() {

        VrmValidator vrmValidator = new VrmValidator();

        assertFalse(vrmValidator.isValid("123456789ABCDE"));
        assertEquals("Registration must be shorter than 14 characters", vrmValidator.getMessage());
    }

    @Test
    @UseDataProvider("validRegistrationNumbers")
    public void isValidReturnsTrueAndNoMessageWhenRegistrationValid(String validRegistration) {

        VrmValidator vrmValidator = new VrmValidator();

        assertTrue(vrmValidator.isValid(validRegistration));
        assertEquals(null, vrmValidator.getMessage());
    }

    @DataProvider
    public static Object[][] validRegistrationNumbers() {

        return new Object[][] {
                {"FN1234"}, {"FN-1234"}, {"F-N--1-2-3-4"}, {"-FN-1234-"},
                {"1234-FN"}, {"F1N23-4"}
        };
    }

    @DataProvider
    public static Object[][] invalidCharactersForRegistration() {

        return new Object[][] {
                {"FN.1234"}, {"FN`1234"}, {"FN~1234"}, {"FN?1234"}, {"FN;1234"},
                {"FN=1234"}, {"FN+1234"}, {"FN_1234"}, {"FN±1234"}, {"FN§1234"},
                {"FN@1234"}, {"FN€1234"}, {"FN#1234"}, {"FN£1234"}, {"FN$1234"},
                {"FN%1234"}, {"FN^1234"}, {"FN&1234"}, {"FN*1234"}, {"FN(1234"},
                {"FN)1234"}, {"FN 1234"}
        };
    }
}
