package uk.gov.dvsa.motr.web.formatting;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM u");

    public static String asDisplayDate(LocalDate date) {
        return date.format(formatter);
    }
}
