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
import uk.gov.dvsa.motr.service.NotificationStatusService;
import uk.gov.dvsa.motr.service.NotifyService;
import uk.gov.dvsa.motr.service.SendStatusReportService;
import uk.gov.dvsa.motr.service.UnsubscribeBouncingContactDetailsService;
import uk.gov.service.notify.NotificationClient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.amazonaws.regions.Region.getRegion;
import static com.amazonaws.regions.Regions.fromName;

import static org.apache.log4j.Level.toLevel;

import static uk.gov.dvsa.motr.SystemVariable.DB_TABLE_CANCELLED_SUBSCRIPTION;
import static uk.gov.dvsa.motr.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.SystemVariable.REGION;
import static uk.gov.dvsa.motr.SystemVariable.STATUS_EMAIL_RECIPIENTS;


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
    public NotificationStatusService provideEmailMessageStatusService(Config config) {

        String apiKey = config.getValue(SystemVariable.GOV_NOTIFY_API_TOKEN);
        NotificationClient client = new NotificationClient(apiKey);

        return new NotificationStatusService(client);
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
    public UnsubscribeBouncingContactDetailsService provideUnsubscribeBouncingEmailAddressService(
            SubscriptionRepository subscriptionRepository,
            CancelledSubscriptionRepository cancelledSubscriptionRepository,
            NotificationStatusService notificationStatusService,
            Config config) {

        return new UnsubscribeBouncingContactDetailsService(subscriptionRepository,
                cancelledSubscriptionRepository,
                notificationStatusService, provideSendStatusReportService(config));
    }

    @Provides
    public SendStatusReportService provideSendStatusReportService(Config config) {

        NotifyService notifyService = provideNotifyService(config);
        List<String> recipients = Arrays.asList(config.getValue(STATUS_EMAIL_RECIPIENTS).replace(" ", "").split(","));


        return new SendStatusReportService(notifyService, recipients);
    }

    @Provides
    public NotifyService provideNotifyService(Config config) {
        String apiKey = config.getValue(SystemVariable.GOV_NOTIFY_API_TOKEN);
        String statusReportEmailTemplateId = config.getValue(SystemVariable.GOV_NOTIFY_STATUS_REPORT_EMAIL_TEMPLATE);
        NotificationClient client = new NotificationClient(apiKey);

        return new NotifyService(client, statusReportEmailTemplateId);

    }

    private static Set<ConfigKey> secretVariables() {

        Set<ConfigKey> secretVariables = new HashSet<>();
        secretVariables.add(SystemVariable.GOV_NOTIFY_API_TOKEN);

        return secretVariables;
    }
}

