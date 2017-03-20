package uk.gov.dvsa.motr.notifier.processing.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SubscriptionDbItemQueueItemRemoverTest {

    private AmazonSQS client = mock(AmazonSQS.class);
    private String queueUrl = "http://fake-queue-url/";
    private QueueItemRemover queueItemRemover;

    @Before
    public void setUp() {
        queueItemRemover = new QueueItemRemover(client, queueUrl);
    }

    @Test
    public void subscriptionMessagePassedForDeletion() throws RemoveSubscriptionFromQueueException {

        SubscriptionQueueItem subscriptionQueueItem = new SubscriptionQueueItem().setMessageReceiptHandle("TEST-RECEIPT-HANDLE");
        DeleteMessageRequest expectedRequest = new DeleteMessageRequest(queueUrl, subscriptionQueueItem.getMessageReceiptHandle());

        queueItemRemover.removeProcessedQueueItem(subscriptionQueueItem);

        verify(client, times(1)).deleteMessage(expectedRequest);
    }
}
