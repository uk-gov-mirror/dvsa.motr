package uk.gov.dvsa.motr.web.test;

import uk.gov.dvsa.motr.encryption.AesCipher;
import uk.gov.dvsa.motr.web.cookie.CookieCipher;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MockCookieCipherFactory implements BaseFactory<AesCipher> {

    @Override
    public AesCipher provide() {

        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        return new CookieCipher(()->encodedKey);
    }
}
