package uk.gov.dvsa.motr.web.serialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalDateDeserialiserTest {

    private LocalDateDeserialiser deserialiser;
    private static final JsonParser PARSER = mock(JsonParser.class);
    private static final DeserializationContext CONTEXT = mock(DeserializationContext.class);

    @Before
    public void setUp() {

        this.deserialiser = new LocalDateDeserialiser();
    }

    @Test
    public void testValidStringDate_returnsLocalDate() throws IOException {

        when(PARSER.getText()).thenReturn("2015/10/13");
        LocalDate actual = deserialiser.deserialize(PARSER, CONTEXT);
        assertEquals(13, actual.getDayOfMonth());
        assertEquals(10, actual.getMonthValue());
        assertEquals(2015, actual.getYear());
    }

    @Test(expected = DateTimeParseException.class)
    public void testInvalidStringDate_throwsJsonProcessingException() throws IOException {

        when(PARSER.getText()).thenReturn("2015/13/10");
        deserialiser.deserialize(PARSER, CONTEXT);
    }
}
