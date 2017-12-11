package uk.gov.dvsa.motr.serialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class LocalDateDeserialiserTest {

    private LocalDateDeserialiser deserialiser;
    private static final JsonParser PARSER = mock(JsonParser.class);
    private static final DeserializationContext CONTEXT = mock(DeserializationContext.class);

    @Before
    public void setUp() {

        this.deserialiser = new LocalDateDeserialiser();
    }

    @Test
    @UseDataProvider("validDateDataProvider")
    public void validStringDate_returnsLocalDate(String year, String month, String day) throws IOException {

        when(PARSER.getText()).thenReturn(String.format("%s-%s-%s", year, month, day));
        LocalDate actual = deserialiser.deserialize(PARSER, CONTEXT);
        assertEquals(Integer.parseInt(day), actual.getDayOfMonth());
        assertEquals(Integer.parseInt(month), actual.getMonthValue());
        assertEquals(Integer.parseInt(year), actual.getYear());
    }


    @Test
    @UseDataProvider("invalidDateDataProvider")
    public void invalidStringDate_throwsJsonProcessingException(String invalidDate) throws IOException {

        when(PARSER.getText()).thenReturn(invalidDate);
        assertNull(deserialiser.deserialize(PARSER, CONTEXT));
    }


    @DataProvider
    public static Object[][] validDateDataProvider() {

        return new Object[][]{
                {"2020", "10", "20"},
                {"2017", "01", "20"},
                {"2012", "01", "31"},
        };
    }

    @DataProvider
    public static Object[][] invalidDateDataProvider() {

        return new Object[][]{
                {null},
                {"asdasdasdas"},
                {"unknown"},
                {""},
        };
    }
}
