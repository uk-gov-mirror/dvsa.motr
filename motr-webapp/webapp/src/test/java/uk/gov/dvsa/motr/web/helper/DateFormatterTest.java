package uk.gov.dvsa.motr.web.helper;

import org.junit.Test;

import uk.gov.dvsa.motr.web.formatting.DateFormatter;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class DateFormatterTest {

    @Test
    public void dateIsReturnedInCorrectFormat() {

        LocalDate date = LocalDate.of(2017, 2, 1);
        String dateString = DateFormatter.asDisplayDate(date);
        assertTrue(dateString.equals("1 February 2017"));
    }
}
