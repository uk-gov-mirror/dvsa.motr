package uk.gov.dvsa.motr.web.cookie;

import uk.gov.dvsa.motr.encryption.AesCipher;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.session.SessionDecryptionFailedEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Encrypts and decrypts CookieSession object using AES GCM cipher.
 */
public class CookieCipher {

    private AesCipher aesCipher;

    public CookieCipher(AesCipher aesCipher) {

        this.aesCipher = aesCipher;
    }

    public byte[] encryptCookie(CookieSession cookieSession) throws IOException {

        return aesCipher.encrypt(toByteArray(cookieSession));
    }

    public CookieSession decryptCookie(byte[] encryptedCookieSession) throws IOException, ClassNotFoundException {

        byte[] decryptedCookieSession;
        try {
            decryptedCookieSession = aesCipher.decrypt(encryptedCookieSession);
        } catch (Exception e) {
            EventLogger.logErrorEvent(new SessionDecryptionFailedEvent(), e);
            throw new IllegalStateException("Could not decrypt the cookie", e);
        }

        return fromByteArray(decryptedCookieSession);
    }

    private byte[] toByteArray(CookieSession cookieSession) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(cookieSession);
        objectOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    private CookieSession fromByteArray(byte[] cookieSession) throws IOException, ClassNotFoundException {

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(cookieSession));
        Object object = objectInputStream.readObject();
        objectInputStream.close();

        return (CookieSession) object;
    }
}
