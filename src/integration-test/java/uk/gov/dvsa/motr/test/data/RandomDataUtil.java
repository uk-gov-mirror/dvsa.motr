package uk.gov.dvsa.motr.test.data;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.random;

public class RandomDataUtil {

    private static final Random random = new Random();

    public static Subscription.ContactType emailOrMobileContactType() {

        return random.nextBoolean() ? Subscription.ContactType.EMAIL : Subscription.ContactType.MOBILE;
    }

    public static String emailOrMobilePhone() {

        return random.nextBoolean() ? email() : mobile();
    }

    private static String email() {

        return UUID.randomUUID().toString().replaceAll("[-]", "") +
                "@" + UUID.randomUUID().toString().replaceAll("[-]", "") + ".com";
    }

    private static String mobile() {

        return random(11, false, false);
    }

    public static String vrm() {

        return random(3, true, false) + random(4, false, true);
    }

    public static String motTestNumber() {
        return random(6, false, true);
    }

    public static String dvlaId() {
        return random(6, false, true);
    }

    public static LocalDate dueDate() {
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2017, 1, 1).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }
}
