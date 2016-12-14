package uk.gov.dvsa.motr.web.config;

import org.junit.Test;

import uk.gov.dvsa.motr.web.encryption.Decryptor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EncryptionAwareConfigTest {

    private static final Config sourceConfig = key -> {
        switch (key.getName()) {
            case "plainKey":
                return Optional.of("plainValue");
            case "secretKey":
                return Optional.of("secretValue");
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
                encryptionAwareConfig.getValue(secretKey).orElse("fail"));
    }

    @Test
    public void returnPlainConfigWithoutDecrypting() {

        assertEquals(
                "Plain value decrypted despite it should not",
                "plainValue",
                encryptionAwareConfig.getValue(() -> "plainKey").orElse("fail"));
    }

    private static Set<ConfigKey> secretKeys() {

        Set<ConfigKey> config = new HashSet<>();
        config.add(secretKey);
        return config;
    }
}
