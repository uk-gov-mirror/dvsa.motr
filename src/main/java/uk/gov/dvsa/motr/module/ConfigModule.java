package uk.gov.dvsa.motr.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.apache.log4j.Logger;

import uk.gov.dvsa.motr.SystemVariable;
import uk.gov.dvsa.motr.config.CachedConfig;
import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.config.ConfigKey;
import uk.gov.dvsa.motr.config.EncryptionAwareConfig;
import uk.gov.dvsa.motr.config.EnvironmentVariableConfig;
import uk.gov.dvsa.motr.encryption.AwsKmsDecryptor;
import uk.gov.dvsa.motr.encryption.Decryptor;
import uk.gov.dvsa.motr.persistence.repository.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository;
import uk.gov.dvsa.motr.service.EmailMessageStatusService;
import uk.gov.dvsa.motr.service.NotifyService;
import uk.gov.dvsa.motr.service.UnsubscribeBouncingEmailAddressService;
import uk.gov.service.notify.NotificationClient;

import java.util.HashSet;
import java.util.Set;

import static com.amazonaws.regions.Region.getRegion;
import static com.amazonaws.regions.Regions.fromName;

import static org.apache.log4j.Level.toLevel;

import static uk.gov.dvsa.motr.SystemVariable.DB_TABLE_CANCELLED_SUBSCRIPTION;
import static uk.gov.dvsa.motr.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.SystemVariable.REGION;


public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {

        Config config = new EnvironmentVariableConfig();
        String region = config.getValue(REGION);
        Decryptor decryptor = new AwsKmsDecryptor(getRegion(fromName(region)));

        config = new CachedConfig(
            new EncryptionAwareConfig(
                config,
                secretVariables(),
                decryptor
            )
        );

        bind(Config.class).toInstance(config);
        Logger.getRootLogger().setLevel(toLevel(config.getValue(SystemVariable.LOG_LEVEL)));
    }

    @Provides
    public EmailMessageStatusService provideEmailMessageStatusService(Config config) {

        String apiKey = config.getValue(SystemVariable.GOV_NOTIFY_API_TOKEN);
        NotificationClient client = new NotificationClient(apiKey);

        return new EmailMessageStatusService(client);
    }

    @Provides
    public SubscriptionRepository provideSubscriptionRepository(Config config) {

        return new SubscriptionRepository(config.getValue(DB_TABLE_SUBSCRIPTION), config.getValue(REGION));
    }

    @Provides
    public CancelledSubscriptionRepository provideCancelledSubscriptionRepository(Config config) {

        return new CancelledSubscriptionRepository(config.getValue(DB_TABLE_CANCELLED_SUBSCRIPTION), config.getValue(REGION));
    }

    @Provides
    public UnsubscribeBouncingEmailAddressService provideUnsubscribeBouncingEmailAddressService(
            SubscriptionRepository subscriptionRepository,
            CancelledSubscriptionRepository cancelledSubscriptionRepository,
            EmailMessageStatusService emailMessageStatusService,
            Config config) {

        return new UnsubscribeBouncingEmailAddressService(subscriptionRepository,
                cancelledSubscriptionRepository,
                emailMessageStatusService);
    }

    @Provides
    public NotifyService provideNotifyService(Config config) {
        String apiKey = config.getValue(SystemVariable.GOV_NOTIFY_API_TOKEN);
        NotificationClient client = new NotificationClient(apiKey);

        return new NotifyService(client);

    }

    private static Set<ConfigKey> secretVariables() {

        Set<ConfigKey> secretVariables = new HashSet<>();
        secretVariables.add(SystemVariable.GOV_NOTIFY_API_TOKEN);

        return secretVariables;
    }
}

