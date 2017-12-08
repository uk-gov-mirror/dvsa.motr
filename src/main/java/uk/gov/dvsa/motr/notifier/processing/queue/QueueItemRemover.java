package uk.gov.dvsa.motr.notifier.processing.queue;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;

public class QueueItemRemover {

    private static final Logger logger = LoggerFactory.getLogger(QueueItemRemover.class);

    private AmazonSQS amazonSyncSqsClient;
    private String subscriptionsQueueUrl;

    public QueueItemRemover(AmazonSQS amazonSyncSqsClient, String subscriptionsQueueUrl) {

        this.amazonSyncSqsClient = amazonSyncSqsClient;
        this.subscriptionsQueueUrl = subscriptionsQueueUrl;
    }

    public void removeProcessedQueueItem(SubscriptionQueueItem subscriptionQueueItem) throws RemoveSubscriptionFromQueueException {

        try {
            String messageRecieptHandle = subscriptionQueueItem.getMessageReceiptHandle();
            DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(subscriptionsQueueUrl, messageRecieptHandle);
            amazonSyncSqsClient.deleteMessage(deleteMessageRequest);

            logger.debug("removed message with receipt handle {}", messageRecieptHandle);
        } catch (Exception e) {
            throw new RemoveSubscriptionFromQueueException(e);
        }
    }
}
