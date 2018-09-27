package uk.gov.dvsa.motr.web.cookie;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.encryption.AesCipher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CookieCipherTest {

    private static final String ATTR_KEY = "key";
    private static final String ATTR_VAL = "value";

    private CookieCipher cookieCipher;
    private AesCipher aesCipher;

    @Before
    public void setUp() {

        aesCipher = mock(AesCipher.class);
        cookieCipher = new CookieCipher(aesCipher);
    }

    @Test
    public void testCookieEncryptionAndDecryption() throws Exception {

        CookieSession originalCookie = createCookieSession();

        when(aesCipher.encrypt(any())).thenReturn(toByteArray(originalCookie));
        when(aesCipher.decrypt(any())).thenReturn(toByteArray(originalCookie));

        byte[] encryptedCookie = cookieCipher.encryptCookie(originalCookie);
        CookieSession decryptedCookie = cookieCipher.decryptCookie(encryptedCookie);

        assertEquals(originalCookie.toString(), decryptedCookie.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void whenAesCipherThrowsExceptionWhenDecryptingShouldThrowIllegalStateException() throws Exception {

        CookieSession originalCookie = createCookieSession();
        byte[] encryptedCookie = cookieCipher.encryptCookie(originalCookie);
        when(aesCipher.decrypt(any())).thenThrow(new IllegalArgumentException());

        cookieCipher.decryptCookie(encryptedCookie);
    }

    private CookieSession createCookieSession() {

        CookieSession cookieSession = new CookieSession();
        cookieSession.setAttribute(ATTR_KEY, ATTR_VAL);
        return cookieSession;
    }

    private byte[] toByteArray(CookieSession cookieSession) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(cookieSession);
        objectOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }
}
