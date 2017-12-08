package uk.gov.dvsa.motr.notifier.processing.receiver;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.queue.QueueItemRemover;
import uk.gov.dvsa.motr.notifier.processing.queue.RemoveSubscriptionFromQueueException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SubscriptionRemoverTest {

    private AmazonSQS client = mock(AmazonSQS.class);
    private String queueUrl = "http://fake-queue-url/";
    private QueueItemRemover subscriptionRemover;

    @Before
    public void setUp() {
        subscriptionRemover = new QueueItemRemover(client, queueUrl);
    }

    @Test
    public void subscriptionMessagePassedForDeletion() throws RemoveSubscriptionFromQueueException {

        SubscriptionQueueItem subscription = new SubscriptionQueueItem().setMessageReceiptHandle("TEST-RECEIPT-HANDLE");
        DeleteMessageRequest expectedRequest = new DeleteMessageRequest(queueUrl, subscription.getMessageReceiptHandle());

        subscriptionRemover.removeProcessedQueueItem(subscription);

        verify(client, times(1)).deleteMessage(expectedRequest);
    }
}
