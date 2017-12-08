package uk.gov.dvsa.motr.notifier.serialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.serialisation.LocalDateDeserialiser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static junit.framework.TestCase.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalDateDeserialiserTest {

    private LocalDateDeserialiser deserialiser;
    private JsonParser parser;
    private DeserializationContext context;

    @Before
    public void setUp() {
        deserialiser = new LocalDateDeserialiser();
        parser = mock(JsonParser.class);
        context = mock(DeserializationContext.class);
    }

    @Test
    public void testValidStringDate_returnsLocalDate() throws IOException {
        when(parser.getText()).thenReturn("2015-10-13");
        LocalDate actual = deserialiser.deserialize(parser, context);
        assertEquals(13, actual.getDayOfMonth());
        assertEquals(10, actual.getMonthValue());
        assertEquals(2015, actual.getYear());
    }

    @Test(expected = DateTimeParseException.class)
    public void testInvalidStringDate_throwsJsonProcessingException() throws IOException {
        when(parser.getText()).thenReturn("10-13-2015");
        deserialiser.deserialize(parser, context);
    }

}
