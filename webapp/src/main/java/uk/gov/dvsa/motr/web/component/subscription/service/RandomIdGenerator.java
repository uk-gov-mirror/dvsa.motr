package uk.gov.dvsa.motr.web.component.subscription.service;

import org.apache.commons.codec.binary.Base32;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class RandomIdGenerator {

    static String getRandomId() {

        SecureRandom random;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot generate random id", e);
        }

        byte[] values = new byte[32];
        Base32 base32 = new Base32();
        random.nextBytes(values);

        return base32.encodeAsString(values).replace("=", "");
    }
}
