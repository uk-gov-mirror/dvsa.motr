package uk.gov.dvsa.motr.notifier.processing.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class SubscriptionsReceiver implements Iterable<SubscriptionQueueItem> {

    private AmazonSQS amazonSyncSqsClient;
    private ReceiveMessageRequest receiveMessageRequest;

    public SubscriptionsReceiver(
            AmazonSQS amazonSyncSqsClient,
            ReceiveMessageRequest receiveMessageRequest) {

        this.amazonSyncSqsClient = amazonSyncSqsClient;
        this.receiveMessageRequest = receiveMessageRequest;
    }

    @Override
    public Iterator<SubscriptionQueueItem> iterator()  {

        try {
            return new SubscriptionIterator(amazonSyncSqsClient, receiveMessageRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class SubscriptionIterator implements Iterator<SubscriptionQueueItem> {

        private AmazonSQS amazonSyncSqsClient;
        private ReceiveMessageRequest receiveMessageRequest;
        private ObjectMapper jsonMapper = new ObjectMapper();
        private int cursor;
        private List<SubscriptionQueueItem> currentBatchOfMessages;

        public SubscriptionIterator(AmazonSQS amazonSyncSqsClient, ReceiveMessageRequest receiveMessageRequest) throws IOException {

            this.amazonSyncSqsClient = amazonSyncSqsClient;
            this.receiveMessageRequest = receiveMessageRequest;
            this.currentBatchOfMessages = getNextBatchOfSubscriptions();
            this.cursor = 0;
        }

        @Override
        public boolean hasNext() {

            return currentBatchOfMessages.size() > 0;
        }

        @Override
        public SubscriptionQueueItem next() {

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            SubscriptionQueueItem currentSubscriptionQueueItem = currentBatchOfMessages.get(cursor);
            cursor++;

            if (cursor == currentBatchOfMessages.size()) {
                currentBatchOfMessages = getNextBatchOfSubscriptions();
                cursor = 0;
            }

            return currentSubscriptionQueueItem;
        }

        private List<SubscriptionQueueItem> getNextBatchOfSubscriptions() {

            return amazonSyncSqsClient.receiveMessage(receiveMessageRequest)
                    .getMessages()
                    .stream()
                    .map(this::getSubscriptionFromMessage)
                    .collect(Collectors.toList());
        }

        private SubscriptionQueueItem getSubscriptionFromMessage(Message message) {
            try {
                SubscriptionQueueItem subscriptionQueueItem = jsonMapper.readValue(message.getBody(), SubscriptionQueueItem.class);
                subscriptionQueueItem.setMessageReceiptHandle(message.getReceiptHandle())
                        .setMessageCorrelationId(message.getMessageAttributes().get("correlation-id").getStringValue());
                return subscriptionQueueItem;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
