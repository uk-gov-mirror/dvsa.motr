package uk.gov.dvsa.motr.web.validator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class MotDueDateValidatorTest {

    public static final int YEAR = 2017;
    public static final int MONTH = 3;
    public static final int DAY_OF_MONTH = 20;
    private LocalDate dateNow;

    @Before
    public void setup() {

        dateNow = LocalDate.of(YEAR, MONTH, DAY_OF_MONTH);
    }

    @Test
    @UseDataProvider("invalidDateDataProvider")
    public void dueDateIsValidReturnsFalseForPastDates(LocalDate invalidDate) {

        MotDueDateValidator validator = new MotDueDateValidator() {
            @Override
            LocalDate getNow() {

                return dateNow;
            }
        };
        assertFalse(validator.isDueDateValid(invalidDate));
    }

    @Test
    @UseDataProvider("validDateDataProvider")
    public void dueDateIsValidReturnsTrueForFutureDates(LocalDate validDate) {

        MotDueDateValidator validator = new MotDueDateValidator() {
            @Override
            LocalDate getNow() {

                return dateNow;
            }
        };
        assertTrue(validator.isDueDateValid(validDate));
    }

    @DataProvider
    public static Object[][] validDateDataProvider() {

        return new Object[][]{
                {LocalDate.of(YEAR, MONTH, DAY_OF_MONTH)},
                {LocalDate.of(YEAR, MONTH, DAY_OF_MONTH + 1)},
                {LocalDate.of(YEAR, MONTH + 1, DAY_OF_MONTH)},
                {LocalDate.of(YEAR + 1, MONTH, DAY_OF_MONTH)},
        };
    }

    @DataProvider
    public static Object[][] invalidDateDataProvider() {

        return new Object[][]{
                {null},
                {LocalDate.of(YEAR, MONTH, DAY_OF_MONTH - 1)},
                {LocalDate.of(YEAR, MONTH - 1, DAY_OF_MONTH)},
                {LocalDate.of(YEAR - 1, MONTH, DAY_OF_MONTH)},
        };
    }
}
