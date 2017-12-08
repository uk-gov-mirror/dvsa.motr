package uk.gov.dvsa.motr.web.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateDisplayHelper {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM u");

    public static String asDisplayDate(LocalDate date) {
        return date.format(formatter);
    }
}
