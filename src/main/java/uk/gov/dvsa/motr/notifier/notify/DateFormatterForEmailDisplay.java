package uk.gov.dvsa.motr.notifier.notify;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatterForEmailDisplay {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM u");

    public static String asFormattedForEmailDate(LocalDate date) {
        return date.format(formatter);
    }
}
