package uk.gov.dvsa.motr.web.validator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class EmailValidatorTest {

    @DataProvider
    public static Object[][] invalidEmails() {
        return new Object[][]{
                {"invalid email"},
                {"invalid@.email.com"},
                {"#@%^%#$@#$@#.com"},
                {"@domain.com"},
                {"email.domain.com"},
                {"email@domain@domain.com"},
                {"email @domain.com"}
        };
    }

    @DataProvider
    public static Object[][] validEmails() {
        return new Object[][]{
                {"test@test.com"},
                {"__@domain.com"},
                {"email@domain.co.jp"},
                {"firstname-lastname@domain.com"},
                {"email@123.123.123.123"}
        };
    }

    @Test
    public void emptyEmailIsInvalid() {

        EmailValidator emailValidator = new EmailValidator("");
        assertFalse(emailValidator.isValid());
        assertSame(EmailValidator.EMAIL_EMPTY_MESSAGE, emailValidator.getMessage());
    }

    @Test
    public void nullEmailIsInvalid() {

        EmailValidator emailValidator = new EmailValidator(null);
        assertFalse(emailValidator.isValid());
        assertSame(EmailValidator.EMAIL_EMPTY_MESSAGE, emailValidator.getMessage());
    }

    @UseDataProvider("invalidEmails")
    @Test
    public void invalidEmailFormatIsInvalid(String email) {

        EmailValidator emailValidator = new EmailValidator(email);
        assertFalse(emailValidator.isValid());
        assertSame(EmailValidator.EMAIL_INVALID_MESSAGE, emailValidator.getMessage());
    }

    @UseDataProvider("validEmails")
    @Test
    public void validEmailFormatIsValid(String email) {

        EmailValidator emailValidator = new EmailValidator(email);
        assertTrue(emailValidator.isValid());
        assertSame(null, emailValidator.getMessage());
    }
}
