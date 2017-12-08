package uk.gov.dvsa.motr.web.helper;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class DateDisplayHelperTest {

    @Test
    public void dateIsReturnedInCorrectFormat() {

        LocalDate date = LocalDate.of(2017, 2, 1);
        String dateString = DateDisplayHelper.asDisplayDate(date);
        assertTrue(dateString.equals("1 February 2017"));
    }
}
