package uk.gov.dvsa.motr.smsreceiver.system.binder.factory;

import uk.gov.dvsa.motr.smsreceiver.config.CachedConfig;
import uk.gov.dvsa.motr.smsreceiver.config.Config;
import uk.gov.dvsa.motr.smsreceiver.config.ConfigKey;
import uk.gov.dvsa.motr.smsreceiver.config.EncryptionAwareConfig;
import uk.gov.dvsa.motr.smsreceiver.config.EnvironmentVariableConfig;
import uk.gov.dvsa.motr.smsreceiver.encryption.Decryptor;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import static org.apache.log4j.Level.toLevel;
import static org.apache.log4j.Logger.getRootLogger;

import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.LOG_LEVEL;

public class MotrConfigFactory implements BaseFactory<Config> {

    private final Decryptor decryptor;

    @Inject
    public MotrConfigFactory(Provider<Decryptor> decryptorProvider) {
        this.decryptor = x -> decryptorProvider.get().decrypt(x);
    }

    private static Set<ConfigKey> secretVariables() {

        return new HashSet<>();
    }

    @Override
    public Config provide() {

        Config config = new CachedConfig(new EncryptionAwareConfig(new EnvironmentVariableConfig(), secretVariables(), decryptor));

        String logLevel = config.getValue(LOG_LEVEL);
        getRootLogger().setLevel(toLevel(logLevel));

        return config;
    }
}
