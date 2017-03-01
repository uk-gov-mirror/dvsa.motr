package uk.gov.dvsa.motr.test.integration.dynamodb;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import java.util.Optional;

public class DynamoDbIntegrationHelper {

    public static String subscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.subscription");
    }

    /**
     * Returns region for DynamoDB dynamoDbClient
     *`
     * @return amazon region
     */
    public static String region() {

        return lookupProperty("test.dynamoDB.integration.region");
    }

    private static String lookupProperty(String property) {

        return Optional.ofNullable(System.getProperty(property)).orElseThrow(
                () -> new RuntimeException("Property: " + property + " is not defined!")
        );
    }

    public static AmazonDynamoDB dynamoDbClient() {

        return AmazonDynamoDBClientBuilder.standard().withRegion(region())
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
    }
}
