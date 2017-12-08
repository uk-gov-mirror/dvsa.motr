package uk.gov.dvsa.motr.config;

import uk.gov.dvsa.motr.encryption.Decryptor;

import java.util.Set;

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
    public String getValue(ConfigKey key) {

        String value = wrappedConfig.getValue(key);
        return encryptedEntries.contains(key) ? decryptor.decrypt(value) : value;
    }
}
