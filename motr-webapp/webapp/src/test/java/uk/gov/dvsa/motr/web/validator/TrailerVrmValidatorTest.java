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
public class TrailerVrmValidatorTest {

    @Test
    public void isValidThrowsErrorMessageWhenNoRegistrationProvided() {

        TrailerVrmValidator vrmValidator = new TrailerVrmValidator();

        assertFalse(vrmValidator.isValid(null));
        assertEquals("Enter the vehicle’s registration", vrmValidator.getMessage());
    }

    @Test
    @UseDataProvider("dataProviderForTrailers")
    public void when_RegistrationMatchTrailers_then_VehicleCanBeTrailer(String registration) {
        TrailerVrmValidator vrmValidator = new TrailerVrmValidator();

        assertTrue(vrmValidator.isValid(registration));
    }

    @Test
    @UseDataProvider("dataProviderForNotTrailers")
    public void when_RegistrationNotMatchTrailers_then_VehicleCanNotBeTrailer(String registration) {
        TrailerVrmValidator vrmValidator = new TrailerVrmValidator();

        assertFalse(vrmValidator.isValid(registration));
    }

    @DataProvider
    public static Object[][] dataProviderForTrailers() {
        return new Object[][]{
                {"12345678"},
                {"98710060"},
                {"A154732"},
                {"A001210"},
                {"a001210"},
                {"C163353"},
                {"c163353"},
        };
    }

    @DataProvider
    public static Object[][] dataProviderForNotTrailers() {
        return new Object[][]{
                {"12421"},
                {"ABD1252"},
                {"abd1252"},
                {"C125A15"},
                {""},
                {"125847437"},
                {"12A5Fs71"},
        };
    }
}
