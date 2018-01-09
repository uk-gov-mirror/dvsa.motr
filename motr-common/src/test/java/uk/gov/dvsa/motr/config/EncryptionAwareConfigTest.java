package uk.gov.dvsa.motr.config;

import org.junit.Test;

import uk.gov.dvsa.motr.encryption.Decryptor;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EncryptionAwareConfigTest {

    private static final Config sourceConfig = key -> {
        switch (key.getName()) {
            case "plainKey":
                return "plainValue";
            case "secretKey":
                return "secretValue";
            default:
                throw new UnsupportedOperationException();
        }
    };

    private static final ConfigKey secretKey = () -> "secretKey";
    private static final Decryptor decryptor = input -> input + "Decrypted";

    private final EncryptionAwareConfig encryptionAwareConfig = new EncryptionAwareConfig(
            sourceConfig,
            secretKeys(),
            decryptor
    );

    @Test
    public void decryptSecretConfigWhenNeeded() {

        assertEquals(
                "Secret not decrypted correctly",
                "secretValueDecrypted",
                encryptionAwareConfig.getValue(secretKey));
    }

    @Test
    public void returnPlainConfigWithoutDecrypting() {

        assertEquals(
                "Plain value decrypted despite it should not",
                "plainValue",
                encryptionAwareConfig.getValue(() -> "plainKey"));
    }

    private static Set<ConfigKey> secretKeys() {

        Set<ConfigKey> config = new HashSet<>();
        config.add(secretKey);
        return config;
    }
}
