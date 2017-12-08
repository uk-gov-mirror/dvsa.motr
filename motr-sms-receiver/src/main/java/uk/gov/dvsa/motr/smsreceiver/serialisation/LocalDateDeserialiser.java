package uk.gov.dvsa.motr.smsreceiver.serialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.time.LocalDate;

public class LocalDateDeserialiser extends JsonDeserializer<LocalDate> {


    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

        try {
            return toDateFromString(jsonParser.getText());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate toDateFromString(String dateAsString) {

        return LocalDate.parse(dateAsString);
    }
}
