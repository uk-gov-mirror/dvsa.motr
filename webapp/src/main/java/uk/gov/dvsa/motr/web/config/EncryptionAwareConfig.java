package uk.gov.dvsa.motr.web.config;

import uk.gov.dvsa.motr.web.encryption.Decryptor;

import java.util.Optional;
import java.util.Set;

import static java.util.function.Function.identity;

/**
 * Wraps a given instance of config and adds decryption layer on top of it.
 */
public class EncryptionAwareConfig implements Config {

    private final Set<ConfigKey> encryptedEntries;

    private final Decryptor decryptor;

    private final Config wrappedConfig;

    public EncryptionAwareConfig(Config wrappedConfig, Set<ConfigKey> encryptedVariables, Decryptor decryptor) {
        this.encryptedEntries = encryptedVariables;
        this.decryptor = decryptor;
        this.wrappedConfig = wrappedConfig;
    }

    @Override
    public Optional<String> getValue(ConfigKey key) {

        return wrappedConfig.getValue(key)
                .map(encryptedEntries.contains(key) ? this::decrypt : identity());

    }

    private String decrypt(String encryptedValue) {

        return decryptor.decrypt(encryptedValue);
    }
}

