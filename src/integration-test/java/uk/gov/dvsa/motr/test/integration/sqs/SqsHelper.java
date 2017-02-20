package uk.gov.dvsa.motr.test.integration.sqs;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper;

import java.util.List;
import java.util.Optional;

public class SqsHelper {

    private AmazonSQSAsync sqsClient;

    public SqsHelper() {
        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration(sqsEndpoint(), DynamoDbIntegrationHelper.region());
        sqsClient = AmazonSQSAsyncClientBuilder.standard().withEndpointConfiguration(endpointConfig)
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
    }

    public List<Message> getMessagesFromQueue() {

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsEndpoint());
        return sqsClient.receiveMessage(receiveMessageRequest).getMessages();
    }

    public void deleteMessageFromQueue(Message message) {

        String messageRecieptHandle = message.getReceiptHandle();
        sqsClient.deleteMessage(new DeleteMessageRequest(sqsEndpoint(), messageRecieptHandle));
    }

    private static String lookupProperty(String property) {

        return Optional.ofNullable(System.getProperty(property)).orElseThrow(
                () -> new RuntimeException("Property: " + property + " is not defined!")
        );
    }

    /**
     * Returns the amount of inflight batches for the sqs queue
     *`
     * @return inflight batches
     */
    public static String inflightBatches() {

        return lookupProperty("test.sqs.integration.inflight.batches");
    }

    /**
     * Returns the amount of delay while purging the amazon sqs queue
     *`
     * @return purge delay
     */
    public static String postPurgeDelay() {

        return lookupProperty("test.sqs.integration.post.purge.delay");
    }

    public static String subscriptionQueue() {

        return lookupProperty("test.sqs.integration.subscription.queue");
    }

    /**
     * Returns the url the sqs queue
     *`
     * @return amazon sqs queue
     */
    public static String sqsEndpoint() {

        return lookupProperty("test.sqs.integration.endpoint");
    }
}
