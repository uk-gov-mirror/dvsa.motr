package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.config.CachedConfig;
import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.config.ConfigKey;
import uk.gov.dvsa.motr.config.EncryptionAwareConfig;
import uk.gov.dvsa.motr.config.EnvironmentVariableConfig;
import uk.gov.dvsa.motr.encryption.Decryptor;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import static org.apache.log4j.Level.toLevel;
import static org.apache.log4j.Logger.getRootLogger;

import static uk.gov.dvsa.motr.web.system.SystemVariable.COOKIE_CIPHER_KEY;
import static uk.gov.dvsa.motr.web.system.SystemVariable.GOV_NOTIFY_API_TOKEN;
import static uk.gov.dvsa.motr.web.system.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN;

public class MotrConfigFactory implements BaseFactory<Config> {

    private final Decryptor decryptor;

    @Inject
    public MotrConfigFactory(Provider<Decryptor> decryptorProvider) {
        this.decryptor = x -> decryptorProvider.get().decrypt(x);
    }

    private static Set<ConfigKey> secretVariables() {

        Set<ConfigKey> secretVariables = new HashSet<>();
        secretVariables.add(GOV_NOTIFY_API_TOKEN);
        secretVariables.add(MOT_TEST_REMINDER_INFO_TOKEN);
        secretVariables.add(COOKIE_CIPHER_KEY);

        return secretVariables;
    }

    @Override
    public Config provide() {

        Config config = new CachedConfig(new EncryptionAwareConfig(new EnvironmentVariableConfig(), secretVariables(), decryptor));

        String logLevel = config.getValue(LOG_LEVEL);
        getRootLogger().setLevel(toLevel(logLevel));

        return config;
    }
}
