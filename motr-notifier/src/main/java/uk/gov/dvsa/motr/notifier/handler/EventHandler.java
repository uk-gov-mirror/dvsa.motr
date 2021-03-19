package uk.gov.dvsa.motr.notifier.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.notifier.module.ConfigModule;
import uk.gov.dvsa.motr.notifier.module.InvocationContextModule;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.unloader.ProcessSubscriptionTask;

import java.io.IOException;
import java.util.List;

public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public void handle(final SQSEvent event, Context context) throws Exception {

        logger.info("Event: {}, context: {}", event, context);
        Injector injector = Guice.createInjector(
                new InvocationContextModule(context),
                new ConfigModule()
        );

        ProcessSubscriptionTask task = injector.getInstance(ProcessSubscriptionTask.class);

        List<SQSEvent.SQSMessage> messages = event.getRecords();

        for (SQSEvent.SQSMessage message : messages) {
            task.run(getSubscriptionFromMessage(message, context));
        }
    }

    private SubscriptionQueueItem getSubscriptionFromMessage(SQSEvent.SQSMessage message, Context context) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        SubscriptionQueueItem subscriptionQueueItem = jsonMapper.readValue(message.getBody(), SubscriptionQueueItem.class);
        subscriptionQueueItem.setMessageReceiptHandle(message.getReceiptHandle())
                .setMessageCorrelationId(context.getAwsRequestId());
        return subscriptionQueueItem;
    }
}
