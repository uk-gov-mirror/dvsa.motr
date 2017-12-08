package uk.gov.dvsa.motr.test.integration.dynamodb;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import static uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables.region;

public class DynamoDbIntegrationHelper {

    /**
     * Returns region for DynamoDB client
     *
     * @return amazon region
     */
    public static AmazonDynamoDB dynamoDbClient() {

        return AmazonDynamoDBClientBuilder.standard().withRegion(region()).build();
    }
}
