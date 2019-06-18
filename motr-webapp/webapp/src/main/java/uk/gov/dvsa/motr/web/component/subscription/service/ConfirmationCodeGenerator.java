package uk.gov.dvsa.motr.web.component.subscription.service;

import java.util.Random;

class ConfirmationCodeGenerator {

    private static final int MIN = 10000;
    private static final int MAX = 99999;

    /**
     * Generates a random number between MIN and MAX.
     *
     * @return int Random integer between MIN and MAX.
     */
    static String generateCode() {

        int randomDigitCode = new Random().nextInt(MAX - MIN + 1) + MIN;
        return Integer.toString(randomDigitCode);
    }
}