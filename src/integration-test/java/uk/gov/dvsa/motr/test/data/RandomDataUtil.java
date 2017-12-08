package uk.gov.dvsa.motr.test.data;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;

import static org.apache.commons.lang3.RandomStringUtils.random;

public class RandomDataUtil {

    public static String mobileNumber() {
        return random(11, false, true);
    }

    public static String motTestNumber() {
        return random(6, false, true);
    }

    public static String vrm() {

        return (random(3, true, false) + random(4, false, true)).replaceAll("\\s+", "").toUpperCase();
    }

    public static LocalDate dueDate() {

        return LocalDate.parse("201" + RandomStringUtils.randomNumeric(1) +
                "-" + "10" +
                "-" + "1" + RandomStringUtils.randomNumeric(1));
    }
}
