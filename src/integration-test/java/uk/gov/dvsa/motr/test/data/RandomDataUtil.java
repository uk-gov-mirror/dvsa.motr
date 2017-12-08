package uk.gov.dvsa.motr.test.data;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.random;

public class RandomDataUtil {

    public static String email() {
        return UUID.randomUUID().toString().replaceAll("[-]", "") +
                "@" + UUID.randomUUID().toString().replaceAll("[-]", "") + ".com";
    }

    public static String motTestNumber() {
        return random(6, false, true);
    }

    public static String vrm() {
        return random(3, true, false) +
                random(4, false, true);
    }

    public static LocalDate dueDate() {
        return LocalDate.parse("201" + RandomStringUtils.randomNumeric(1) +
                "-" + "10" +
                "-" + "1" + RandomStringUtils.randomNumeric(1));
    }

}
