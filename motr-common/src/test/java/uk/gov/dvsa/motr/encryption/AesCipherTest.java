package uk.gov.dvsa.motr.encryption;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.assertTrue;

public class AesCipherTest {

    private static AesCipher aesCipher;

    @Before
    public void setUp() {

        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        aesCipher = new AesCipher(() -> encodedKey);
    }

    @Test
    public void testEncryptionAndDecryption() throws Exception {

        byte[] objectToEncrypt = createObjectForEncryption();

        byte[] encryptedObject = aesCipher.encrypt(objectToEncrypt);
        byte[] decryptedObject = aesCipher.decrypt(encryptedObject);

        assertTrue(Arrays.equals(objectToEncrypt, decryptedObject));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenEncryptedObjectIsTrimmedShouldThrowIllegalArgumentException() throws Exception {

        byte[] objectToEncrypt = createObjectForEncryption();

        byte[] encryptedObject = aesCipher.encrypt(objectToEncrypt);
        byte[] alteredEncryptedObject = Arrays.copyOfRange(encryptedObject, 4, encryptedObject.length);

        aesCipher.decrypt(alteredEncryptedObject);
    }

    @Test(expected = AEADBadTagException.class)
    public void whenEncryptedObjectIsAlteredShouldThrowAeadBadTagException() throws Exception {

        byte[] objectToEncrypt = createObjectForEncryption();

        byte[] encryptedObject = aesCipher.encrypt(objectToEncrypt);

        byte[] additionalBytes = new byte[] {1, 2, -5, 6, 7};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(encryptedObject);
        outputStream.write(additionalBytes);
        byte[] alteredEncryptedObject = outputStream.toByteArray();

        aesCipher.decrypt(alteredEncryptedObject);
    }

    private byte[] createObjectForEncryption() {

        byte [] objectToEncrypt = new byte[16];
        new Random().nextBytes(objectToEncrypt);

        return objectToEncrypt;
    }
}
