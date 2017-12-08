package uk.gov.dvsa.motr.dates.formatting;

import org.junit.Test;

import uk.gov.dvsa.motr.notifier.notify.DateFormatterForEmailDisplay;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DateFormatterForEmailDisplayTest {

    @Test
    public void dateIsReturnedInTheCorrectFormat() {

        LocalDate date = LocalDate.of(2017, 7, 10);
        assertEquals("10 July 2017", DateFormatterForEmailDisplay.asFormattedForEmailDate(date));
    }
}
