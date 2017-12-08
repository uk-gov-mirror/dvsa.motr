package uk.gov.dvsa.motr.web.validator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class EmailValidatorTest {

    private EmailValidator emailValidator;

    @DataProvider
    public static Object[][] invalidEmails() {
        return new Object[][]{
                {"invalid email"},
                {"invalid@.email.com"},
                {"#@%^%#$@#$@#.com"},
                {"@domain.com"},
                {"email.domain.com"},
                {"email@domain@domain.com"},
                {"email@domain.;"},
                {"email@domain"},
                {"email@domain..com"},
                {"email@domain.123"},
                {"email@domain.0123456789012345678901234567890123456789012345678901234567890123.com"},
                {"email @domain.com"}
        };
    }

    @DataProvider
    public static Object[][] validEmails() {
        return new Object[][]{
                {"test@test.com"},
                {"__@domain.com"},
                {"email@domain.co.jp"},
                {"email@do©ßmain.com"},
                {"email@d©ßain.co.uk"},
                {"email@例え.テスト"},
                {"email@domain.012345678901234567890123456789012345678901234567890123456789012.com"},
                {"email@DOMAIN.COM"},
                {"email@DOMAIN.com"},
                {"EMAIL@domain.COM"},
                {"firstname-lastname@domain.com"}
        };
    }

    @Before
    public void setUp() {

        this.emailValidator = new EmailValidator();
    }

    @Test
    public void emptyEmailIsInvalid() {

        assertFalse(emailValidator.isValid(""));
        assertSame(EmailValidator.EMAIL_EMPTY_MESSAGE, emailValidator.getMessage());
    }

    @Test
    public void nullEmailIsInvalid() {

        assertFalse(emailValidator.isValid(null));
        assertSame(EmailValidator.EMAIL_EMPTY_MESSAGE, emailValidator.getMessage());
    }

    @UseDataProvider("invalidEmails")
    @Test
    public void invalidEmailFormatIsInvalid(String email) {

        assertFalse(emailValidator.isValid(email));
        assertSame(EmailValidator.EMAIL_INVALID_MESSAGE, emailValidator.getMessage());
    }

    @UseDataProvider("validEmails")
    @Test
    public void validEmailFormatIsValid(String email) {

        assertTrue(emailValidator.isValid(email));
        assertSame(null, emailValidator.getMessage());
    }

    @Test
    public void tooLongEmailIsInvalid() {

        String email = RandomStringUtils.random(266);
        assertFalse(emailValidator.isValid(email));
        assertSame(EmailValidator.EMAIL_INVALID_MESSAGE, emailValidator.getMessage());
    }
}
