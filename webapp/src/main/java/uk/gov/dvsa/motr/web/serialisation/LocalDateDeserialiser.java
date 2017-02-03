package uk.gov.dvsa.motr.web.serialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserialiser extends JsonDeserializer<LocalDate> {

    private static final String DATE_FORMAT = "yyyy/MM/dd";

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        return toDateFromString(jsonParser.getText());
    }

    private LocalDate toDateFromString(String dateAsString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return LocalDate.parse(dateAsString, formatter);
    }
}
