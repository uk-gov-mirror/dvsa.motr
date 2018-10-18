package uk.gov.dvsa.motr.encryption;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implements Advanced Encryption Standard (AES) cipher in Galois/Counter Mode (GCM) with no padding.
 * Uses 12 byte long Initialization Vector which is randomly generated and enforces use of 128 bit long authentication tag.
 * Requires SecretKey to be passed, can be used to encrypt and decrypt data converted to byte array.
 * Useful information about implementation of AES:
 * https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 */
public class AesCipher {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int INITIALIZATION_VECTOR_LENGTH_BYTE = 12;

    private final SecretKey secretKey;
    private Cipher cipher;

    public AesCipher(String cipherKey) {

        byte[] decodedCipherKey = Base64.getDecoder().decode(cipherKey);
        secretKey = new SecretKeySpec(decodedCipherKey, 0, decodedCipherKey.length, "AES");
    }

    public byte[] encrypt(byte[] plainText) {

        byte[] iv = createInitializationVector();

        cipher = getCipher(iv, Cipher.ENCRYPT_MODE);

        byte[] cipherText;
        try {
            cipherText = cipher.doFinal(plainText);
        } catch (Exception e) {
            throw new IllegalStateException("Could not encrypt the data", e);
        }

        return concatenateTextAndInitializationVector(cipherText, iv);
    }

    public byte[] decrypt(byte[] cipherMessage) throws IllegalBlockSizeException, BadPaddingException {

        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
        byte[] iv = getInitializationVectorFromCipherMessage(byteBuffer);
        byte[] cipherText = getEncryptedTextFromCipherMessage(byteBuffer);

        cipher = getCipher(iv, Cipher.DECRYPT_MODE);

        byte[] decryptedText;
        decryptedText = cipher.doFinal(cipherText);

        return decryptedText;
    }

    private Cipher getCipher(byte[] initializationVector, int mode) {

        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, initializationVector);

        try {
            if (cipher == null) {
                cipher = Cipher.getInstance(ALGORITHM);
            }
            cipher.init(mode, secretKey, parameterSpec);
        } catch (Exception e) {
            throw new IllegalStateException("Could not initiate Cipher instance", e);
        }

        return cipher;
    }

    private byte[] createInitializationVector() {

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[INITIALIZATION_VECTOR_LENGTH_BYTE];
        secureRandom.nextBytes(iv);

        return iv;
    }

    private byte[] concatenateTextAndInitializationVector(byte[] cipherText, byte[] iv) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
        byteBuffer.putInt(iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);

        return byteBuffer.array();
    }

    private byte[] getInitializationVectorFromCipherMessage(ByteBuffer byteBuffer) {

        int ivLength = byteBuffer.getInt();
        if (ivLength < 12 || ivLength >= 16) {
            throw new IllegalArgumentException("Invalid initialization vector length");
        }

        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);

        return iv;
    }

    private byte[] getEncryptedTextFromCipherMessage(ByteBuffer byteBuffer) {

        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        return cipherText;
    }
}
