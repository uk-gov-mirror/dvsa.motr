package uk.gov.dvsa.motr.notifier.module;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import org.apache.log4j.Logger;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.config.CachedConfig;
import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.config.ConfigKey;
import uk.gov.dvsa.motr.config.EncryptionAwareConfig;
import uk.gov.dvsa.motr.config.EnvironmentVariableConfig;

import uk.gov.dvsa.motr.encryption.AwsKmsDecryptor;
import uk.gov.dvsa.motr.encryption.Decryptor;
import uk.gov.dvsa.motr.executor.BlockingExecutor;
import uk.gov.dvsa.motr.notifier.SystemVariable;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.notify.NotifyEmailService;
import uk.gov.dvsa.motr.notifier.notify.NotifySmsService;
import uk.gov.dvsa.motr.notifier.processing.queue.QueueItemRemover;
import uk.gov.dvsa.motr.notifier.processing.queue.SubscriptionsReceiver;
import uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService;
import uk.gov.dvsa.motr.notifier.processing.unloader.QueueUnloader;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.service.notify.NotificationClient;

import java.util.HashSet;
import java.util.Set;

import static com.amazonaws.regions.Region.getRegion;
import static com.amazonaws.regions.Regions.fromName;

import static org.apache.log4j.Level.toLevel;

import static uk.gov.dvsa.motr.notifier.SystemVariable.CHECKSUM_SALT;
import static uk.gov.dvsa.motr.notifier.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.notifier.SystemVariable.EU_GO_LIVE_DATE;
import static uk.gov.dvsa.motr.notifier.SystemVariable.GOV_NOTIFY_API_TOKEN;
import static uk.gov.dvsa.motr.notifier.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MESSAGE_RECEIVE_TIMEOUT;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MESSAGE_VISIBILITY_TIMEOUT;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MOTH_DIRECT_URL_PREFIX;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MOT_API_DVLA_ID_URI;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MOT_API_MOT_TEST_NUMBER_URI;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN;
import static uk.gov.dvsa.motr.notifier.SystemVariable.ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU;
import static uk.gov.dvsa.motr.notifier.SystemVariable.ONE_MONTH_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU;
import static uk.gov.dvsa.motr.notifier.SystemVariable.REGION;
import static uk.gov.dvsa.motr.notifier.SystemVariable.REMAINING_TIME_THRESHOLD;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SMS_ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SMS_ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SMS_ONE_MONTH_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SMS_ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SMS_TWO_WEEK_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SMS_TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SUBSCRIPTIONS_QUEUE_URL;
import static uk.gov.dvsa.motr.notifier.SystemVariable.TWO_WEEK_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU;
import static uk.gov.dvsa.motr.notifier.SystemVariable.VEHICLE_API_CLIENT_TIMEOUT;
import static uk.gov.dvsa.motr.notifier.SystemVariable.WEB_BASE_URL;
import static uk.gov.dvsa.motr.notifier.SystemVariable.WORKER_COUNT;

public class ConfigModule extends AbstractModule {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigModule.class);
    private static final int MAX_NUMBER_OF_MESSAGES = 10;
    private static final int POST_PROCESSING_DELAY_MS = 30000;
    private AmazonSQS sqsClient;
    private NotificationClient client;

    @Override
    protected void configure() {

        Config config = new EnvironmentVariableConfig();
        String region = config.getValue(REGION);
        Decryptor decryptor =  new AwsKmsDecryptor(getRegion(fromName(region)));

        config = new CachedConfig(
                new EncryptionAwareConfig(
                        config,
                        secretVariables(),
                        decryptor
                )
        );

        bind(Config.class).toInstance(config);
        Logger.getRootLogger().setLevel(toLevel(config.getValue(LOG_LEVEL)));

        sqsClient = AmazonSQSClientBuilder.defaultClient();

        client = new NotificationClient(config.getValue(GOV_NOTIFY_API_TOKEN));
    }

    @Provides
    public QueueUnloader provideUnloader(
            SubscriptionsReceiver subscriptionsReceiver,
            QueueItemRemover queueItemRemover,
            ProcessSubscriptionService processSubscriptionService,
            Config config) {

        logger.info("Worker count is {}", Integer.parseInt(config.getValue(WORKER_COUNT)));
        BlockingExecutor executor =
                new BlockingExecutor(Integer.parseInt(config.getValue(WORKER_COUNT)));

        return new QueueUnloader(subscriptionsReceiver, queueItemRemover, executor,
                processSubscriptionService, Integer.parseInt(config.getValue(REMAINING_TIME_THRESHOLD)) * 1000,
                POST_PROCESSING_DELAY_MS);
    }

    @Provides
    public SubscriptionsReceiver provideSubscriptionReceiver(Config config) {

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(config.getValue(SUBSCRIPTIONS_QUEUE_URL));
        receiveMessageRequest.setWaitTimeSeconds(Integer.parseInt(config.getValue(MESSAGE_RECEIVE_TIMEOUT)));
        receiveMessageRequest.setMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES);
        receiveMessageRequest.setVisibilityTimeout(Integer.parseInt(config.getValue(MESSAGE_VISIBILITY_TIMEOUT)));
        receiveMessageRequest.withMessageAttributeNames("correlation-id");

        return new SubscriptionsReceiver(sqsClient, receiveMessageRequest);
    }

    @Provides
    public QueueItemRemover provideSubscriptionRemover(Config config) {

        return new QueueItemRemover(sqsClient ,config.getValue(SUBSCRIPTIONS_QUEUE_URL));
    }

    @Provides
    public VehicleDetailsClient provideVehicleDetailsClient(Config config) {

        int timeoutInMs = Integer.parseInt(config.getValue(VEHICLE_API_CLIENT_TIMEOUT)) * 1000;

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(Integer.parseInt(config.getValue(WORKER_COUNT)));

        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig = clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, poolingHttpClientConnectionManager);
        clientConfig = clientConfig.property(ClientProperties.CONNECT_TIMEOUT, timeoutInMs);
        clientConfig = clientConfig.property(ClientProperties.READ_TIMEOUT, timeoutInMs);

        return new VehicleDetailsClient(clientConfig, config.getValue(MOT_TEST_REMINDER_INFO_TOKEN))
            .withByMotTestNumberUri(config.getValue(MOT_API_MOT_TEST_NUMBER_URI))
            .withByDvlaIdUri(config.getValue(MOT_API_DVLA_ID_URI));
    }

    @Provides
    public ProcessSubscriptionService provideHandleSubscriptionService(
            VehicleDetailsClient client,
            SubscriptionRepository repository,
            NotifyEmailService notifyEmailService,
            NotifySmsService notifySmsService,
            Config config) {

        String webBaseUrl = config.getValue(WEB_BASE_URL);
        String mothDirectUrlPrefix = config.getValue(MOTH_DIRECT_URL_PREFIX);
        String checksumSalt = config.getValue(CHECKSUM_SALT);

        return new ProcessSubscriptionService(client, repository, notifyEmailService, notifySmsService, webBaseUrl,
                mothDirectUrlPrefix, checksumSalt);
    }

    @Provides
    public SubscriptionRepository provideSubscriptionRepository(Config config) {

        return new DynamoDbSubscriptionRepository(config.getValue(DB_TABLE_SUBSCRIPTION), config.getValue(REGION));
    }

    @Provides
    public NotifyEmailService provideNotifyEmailService(Config config) {

        String oneMonthTemplateId = config.getValue(ONE_MONTH_NOTIFICATION_TEMPLATE_ID);
        String twoWeekTemplateId = config.getValue(TWO_WEEK_NOTIFICATION_TEMPLATE_ID);
        String oneDayAfterTemplateId = config.getValue(ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID);
        String oneMonthTemplateIdPostEu = config.getValue(ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU);
        String twoWeekTemplateIdPostEu = config.getValue(TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU);
        String oneDayAfterTemplateIdPostEu = config.getValue(ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU);
        String euGoLiveDate = config.getValue(EU_GO_LIVE_DATE);
        NotifyTemplateEngine notifyTemplateEngine = new NotifyTemplateEngine();

        return new NotifyEmailService(client, oneMonthTemplateId, twoWeekTemplateId, oneDayAfterTemplateId,
        oneMonthTemplateIdPostEu, twoWeekTemplateIdPostEu, oneDayAfterTemplateIdPostEu, euGoLiveDate, notifyTemplateEngine);
    }

    @Provides
    public NotifySmsService provideNotifySmsService(Config config) {

        String oneMonthTemplateId = config.getValue(SMS_ONE_MONTH_NOTIFICATION_TEMPLATE_ID);
        String twoWeekTemplateId = config.getValue(SMS_TWO_WEEK_NOTIFICATION_TEMPLATE_ID);
        String oneDayAfterTemplateId = config.getValue(SMS_ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID);
        String oneMonthTemplateIdPostEu = config.getValue(SMS_ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU);
        String twoWeekTemplateIdPostEu = config.getValue(SMS_TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU);
        String oneDayAfterTemplateIdPostEu = config.getValue(SMS_ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU);
        String euGoLiveDate = config.getValue(EU_GO_LIVE_DATE);
        NotifyTemplateEngine notifyTemplateEngine = new NotifyTemplateEngine();

        return new NotifySmsService(client, oneMonthTemplateId, twoWeekTemplateId, oneDayAfterTemplateId, oneMonthTemplateIdPostEu,
                twoWeekTemplateIdPostEu, oneDayAfterTemplateIdPostEu, euGoLiveDate, notifyTemplateEngine);
    }

    @Provides
    public Decryptor provideDecryptor(Config config) {

        return new AwsKmsDecryptor(getRegion(fromName(config.getValue(REGION))));
    }

    private static Set<ConfigKey> secretVariables() {

        Set<ConfigKey> secretVariables = new HashSet<>();
        secretVariables.add(SystemVariable.GOV_NOTIFY_API_TOKEN);
        secretVariables.add(SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN);
        secretVariables.add(SystemVariable.CHECKSUM_SALT);

        return secretVariables;
    }
}
