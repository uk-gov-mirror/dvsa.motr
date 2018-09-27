package uk.gov.dvsa.motr.web.cookie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class CookieCipherTest {

    private static final String ATTR_KEY = "key";
    private static final String ATTR_VAL = "value";

    private CookieCipher cookieCipher;

    @Before
    public void setUp() {

        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        cookieCipher = new CookieCipher(encodedKey);
    }

    @Test
    public void testCookieEncryptionAndDecryption() throws Exception {

        CookieSession originalCookie = createCookieSession();

        byte[] encryptedCookie = cookieCipher.encryptCookie(originalCookie);
        CookieSession decryptedCookie = cookieCipher.decryptCookie(encryptedCookie);

        assertEquals(originalCookie.toString(), decryptedCookie.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void whenAesCipherThrowsExceptionWhenDecryptingShouldThrowIllegalStateException() throws Exception {

        CookieSession originalCookie = createCookieSession();
        byte[] encryptedCookie = cookieCipher.encryptCookie(originalCookie);
        final CookieCipher cookieCipherSpy = Mockito.spy(cookieCipher);
        Mockito.doThrow(new IllegalArgumentException()).when(cookieCipherSpy).decrypt(any());

        cookieCipherSpy.decryptCookie(encryptedCookie);
    }

    private CookieSession createCookieSession() {

        CookieSession cookieSession = new CookieSession();
        cookieSession.setAttribute(ATTR_KEY, ATTR_VAL);
        return cookieSession;
    }
}
