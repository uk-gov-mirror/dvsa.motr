package uk.gov.dvsa.motr.web.serialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateDeserialiser extends JsonDeserializer<LocalDate> {


    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        return toDateFromString(jsonParser.getText());
    }

    private LocalDate toDateFromString(String dateAsString) {

        return LocalDate.parse(dateAsString);
    }
}
