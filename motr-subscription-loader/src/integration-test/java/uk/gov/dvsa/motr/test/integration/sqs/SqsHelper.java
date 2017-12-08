package uk.gov.dvsa.motr.test.integration.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Optional;

public class SqsHelper {

    private AmazonSQSAsync sqsClient;

    public SqsHelper() {

        sqsClient = AmazonSQSAsyncClientBuilder.standard().build();
    }

    public List<Message> getMessagesFromQueue() {

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsEndpoint());
        return sqsClient.receiveMessage(receiveMessageRequest).getMessages();
    }

    public void deleteMessageFromQueue(Message message) {

        String messageRecieptHandle = message.getReceiptHandle();
        sqsClient.deleteMessage(new DeleteMessageRequest(sqsEndpoint(), messageRecieptHandle));
    }

    /**
     * Returns the url the sqs queue
     *`
     * @return amazon sqs queue
     */
    public static String sqsEndpoint() {

        return lookupProperty("test.sqs.integration.endpoint");
    }
    
    private static String lookupProperty(String property) {

        return Optional.ofNullable(System.getProperty(property)).orElseThrow(
                () -> new RuntimeException("Property: " + property + " is not defined!")
        );
    }
}
