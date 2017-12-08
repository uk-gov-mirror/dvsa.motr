package uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class Dispatcher {

    private AmazonSQSAsync sqsClient;
    private String queueUrl;
    private ObjectMapper jsonMapper;
    private Map<String, MessageAttributeValue> attributes = new HashMap<>();

    @Inject
    public Dispatcher(AmazonSQSAsync client, String queueUrl, String correlationId) {

        this.sqsClient = client;
        this.queueUrl = queueUrl;
        this.jsonMapper = new ObjectMapper();
        this.attributes.put("correlation-id",
                new MessageAttributeValue().withDataType("String").withStringValue(correlationId));
    }

    public DispatchResult dispatch(Subscription subscription) {

        String messageBody;

        try {
            messageBody = jsonMapper.writeValueAsString(subscription);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error of subscription");
        }

        SendMessageRequest request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageAttributes(attributes)
                .withMessageBody(messageBody);

        return new DispatchResult(subscription, sqsClient.sendMessageAsync(request));
    }
}
