package uk.gov.dvsa.motr.notify;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.EnumSet;

public class PreservationDateChecker {

    private static final MonthDay JAN_29 = MonthDay.of(1,29);
    private static final MonthDay JAN_30 = MonthDay.of(1,30);
    private static final MonthDay JAN_31 = MonthDay.of(1,31);
    private static final MonthDay FEB_28 = MonthDay.of(2,28);
    private static final MonthDay FEB_29 = MonthDay.of(2,29);
    private static final EnumSet<Month> shortMonths = EnumSet.of(Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER);

    public static boolean isValidPreservationDate(LocalDate referenceDate) {

        MonthDay dateToTest = MonthDay.from(referenceDate);

        if (dateToTest.equals(JAN_30) || dateToTest.equals(JAN_31)) {
            return false;
        } else if (!referenceDate.isLeapYear() && dateToTest.equals(JAN_29)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean dateIs28FebruaryButNotLeapYear(LocalDate referenceDate) {

        return (MonthDay.from(referenceDate).equals(FEB_28)) && !referenceDate.isLeapYear();
    }

    public static boolean dateIs29February(LocalDate referenceDate) {

        return (MonthDay.from(referenceDate).equals(FEB_29));
    }

    public static boolean expiryMonthIsLongerThanPreviousMonthButNotMarch(LocalDate referenceDate) {
        Month referenceDateMonth = referenceDate.getMonth();
        int dayOfMonth = referenceDate.getDayOfMonth();

        return shortMonths.contains(referenceDateMonth) && dayOfMonth == 30;
    }
}
