package uk.gov.dvsa.motr.dates.formatting;

import org.junit.Test;

import uk.gov.dvsa.motr.notifier.notify.DateFormatterForSmsDisplay;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DateFormatterForSmsDisplayTest {

    @Test
    public void dateIsReturnedInTheCorrectFormat() {

        LocalDate date = LocalDate.of(2017, 7, 10);
        assertEquals("10/07/17", DateFormatterForSmsDisplay.asFormattedForSmsDate(date));
    }
}
