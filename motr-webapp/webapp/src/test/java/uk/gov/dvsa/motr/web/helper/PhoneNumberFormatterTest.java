package uk.gov.dvsa.motr.web.helper;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.web.formatting.PhoneNumberFormatter;

import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class PhoneNumberFormatterTest {

    @Test
    @UseDataProvider("dataProviderPhoneNumber")
    public void phoneNumberProvidedIsParsedAndFormattedCorrectly(String phoneNumber, String expectedNormalizedUkPhoneNumber) {

        String normalizedUkPhoneNumber = PhoneNumberFormatter.normalizeUkPhoneNumber(phoneNumber);
        assertEquals(expectedNormalizedUkPhoneNumber, normalizedUkPhoneNumber);
    }

    @DataProvider
    public static Object[][] dataProviderPhoneNumber() {

        return new Object[][] {
                { "07123456789", "07123456789" },
                { "+44 7123456789", "07123456789" },
                { "44 7123456789", "07123456789" },
                { "0044 7123456789", "07123456789" },
                { "(44)7123 456 789", "07123456789" },
                { "(+44)7123 456 789", "07123456789" },
                { "712345678910", "712345678910" },
                { "+44 (0)7123 456 789 10", "712345678910" },
                { "004471234567", "71234567" },
                { "0712345678", "712345678" },
                { "+44 8081 570364", "08081570364" },
                { "020 7946 0991", "02079460991" },
                { " ", null },
                { "07123 ☟☜⬇⬆☞☝", "07123" },
                { "ALPHANUM3R1C", null },
                { "07123 456789...", "07123456789" }
        };
    }
}