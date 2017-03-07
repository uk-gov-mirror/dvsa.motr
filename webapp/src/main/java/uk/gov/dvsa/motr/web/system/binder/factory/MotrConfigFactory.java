package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.web.config.CachedConfig;
import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.config.ConfigKey;
import uk.gov.dvsa.motr.web.config.EncryptionAwareConfig;
import uk.gov.dvsa.motr.web.config.EnvironmentVariableConfig;
import uk.gov.dvsa.motr.web.encryption.Decryptor;
import uk.gov.dvsa.motr.web.system.SystemVariable;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

public class MotrConfigFactory implements BaseFactory<Config> {

    private final Decryptor decryptor;

    @Inject
    public MotrConfigFactory(Provider<Decryptor> decryptorProvider) {
        this.decryptor = x -> decryptorProvider.get().decrypt(x);
    }

    private static Set<ConfigKey> secretVariables() {

        Set<ConfigKey> secretVariables = new HashSet<>();
        secretVariables.add(SystemVariable.GOV_NOTIFY_API_TOKEN);
        secretVariables.add(SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN);

        return secretVariables;
    }

    @Override
    public Config provide() {

        return new CachedConfig(
                new EncryptionAwareConfig(
                        new EnvironmentVariableConfig(),
                        secretVariables(),
                        decryptor
                )
        );
    }
}
