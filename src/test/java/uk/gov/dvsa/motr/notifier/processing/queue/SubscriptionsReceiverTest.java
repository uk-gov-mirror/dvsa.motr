package uk.gov.dvsa.motr.notifier.processing.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionsReceiverTest  {

    private AmazonSQS amazonSyncSqsClient = mock(AmazonSQS.class);
    private ReceiveMessageRequest receiveMessageRequest = mock(ReceiveMessageRequest.class);

    @Test
    public void hasNextIsTrueIfThereAreMessagesCurrentlyInBatch() throws JsonProcessingException {

        when(amazonSyncSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(mockRecieveMessageResult());
        assertTrue(subscriptionsReceiver().iterator().hasNext());
    }

    @Test
    public void hasNextIsFalseIfThereAreNoMessagesCurrentlyInBatch() {

        when(amazonSyncSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(new ReceiveMessageResult());
        assertFalse(subscriptionsReceiver().iterator().hasNext());
    }

    @Test
    public void nextWillReturnNextSubscriptionInBatch() throws JsonProcessingException {

        when(amazonSyncSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(mockRecieveMessageResult());
        SubscriptionQueueItem actual = subscriptionsReceiver().iterator().next();

        assertEquals("Test-receipt-handle", actual.getMessageReceiptHandle());
        assertEquals("test-vrm", actual.getVrm());
    }

    @Test
    public void whenBatchHasOneItemLeftRetrieveNewBatch() throws JsonProcessingException {

        when(amazonSyncSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(mockRecieveMessageResult());

        subscriptionsReceiver().iterator().next();

        // Once in constructor.  The second in next() because it is empty
        verify(amazonSyncSqsClient, times(2)).receiveMessage(any(ReceiveMessageRequest.class));
    }

    private ReceiveMessageResult mockRecieveMessageResult() throws JsonProcessingException {

        Message message = new Message();
        message.setBody(mockMessageBody());
        HashMap<String, MessageAttributeValue> attributeValueHashMap = new HashMap<>();
        attributeValueHashMap.put("correlation-id", new MessageAttributeValue().withStringValue("test-correlation-id"));
        message.setMessageAttributes(attributeValueHashMap);
        message.setReceiptHandle("Test-receipt-handle");

        ReceiveMessageResult receiveMessageResult = new ReceiveMessageResult();
        return receiveMessageResult.withMessages(message);
    }

    private String mockMessageBody() throws JsonProcessingException {

        SubscriptionQueueItem subscriptionQueueItem = new SubscriptionQueueItem()
                .setMessageReceiptHandle("Test-receipt-handle")
                .setVrm("test-vrm")
                .setContactDetail("test@email.com")
                .setId("test-id")
                .setMotTestNumber("test-mot-number-123")
                .setMessageCorrelationId("test-correlation-id");

        return new ObjectMapper().writeValueAsString(subscriptionQueueItem);
    }

    private SubscriptionsReceiver subscriptionsReceiver() {

        return new SubscriptionsReceiver(amazonSyncSqsClient, receiveMessageRequest);
    }
}
