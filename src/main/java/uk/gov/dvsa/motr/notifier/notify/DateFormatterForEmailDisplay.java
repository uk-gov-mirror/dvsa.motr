package uk.gov.dvsa.motr.notifier.notify;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatterForEmailDisplay {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM u");
    private static DateTimeFormatter formatterWithoutYear = DateTimeFormatter.ofPattern("d MMMM");

    public static String asFormattedForEmailDate(LocalDate date) {
        return date.format(formatter);
    }

    public static String asFormattedForEmailDateWithoutYear(LocalDate date) {
        return date.format(formatterWithoutYear);
    }
}
