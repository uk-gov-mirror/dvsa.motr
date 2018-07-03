package uk.gov.dvsa.motr.conversion;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class DataAnonymizerTest {

    private static final String CONTACT_DATA = "987654321";

    private DataAnonymizer dataAnonymizer;

    @DataProvider
    public static Object[][] contactData() {
        return new Object[][]{
                {"123456789", "d9e6762dd1c8eaf6d61b3c6192fc408d4d6d5f1176d0c29169bc24e71c3f274ad27fcd5811b313d681f7e55ec02d73d499c95455b6" +
                        "b5bb503acf574fba8ffe85"},
                {"example@mail.com", "fd79238874f08e3b3734b06db710e8e4a890fc6d47b0631683e78531e828c19a83a80b92cc7f6c38b2c2e4a8fc112177e15" +
                        "6fe92baf8005d4b54ca43338bdf16"}
        };
    }

    @Before
    public void setup() {
        this.dataAnonymizer = new DataAnonymizer();
    }

    @UseDataProvider("contactData")
    @Test
    public void anonymizeContactDataShouldReturnHashedString(String contactData, String expectedResult) {
        String result = dataAnonymizer.anonymizeContactData(contactData);

        assertEquals(expectedResult, result);
    }

    @Test
    public void anonymizeContactDataShouldReturnTheSameHashedStringForTheSameContactData() {
        String firstResult = dataAnonymizer.anonymizeContactData(CONTACT_DATA);
        String secondResult = dataAnonymizer.anonymizeContactData(CONTACT_DATA);

        assertEquals(firstResult, secondResult);
    }
}
