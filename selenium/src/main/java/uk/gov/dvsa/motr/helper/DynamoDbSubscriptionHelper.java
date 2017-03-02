package uk.gov.dvsa.motr.helper;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.config.Configurator;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DynamoDbSubscriptionHelper {

    public static String addSubscription(String vrm, String email) {

        String tableName = Configurator.dynamoDbTableName();
        String region = Configurator.dynamoDbRegion();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain()).withRegion(region).build();
        DynamoDB dynamoDb = new DynamoDB(client);

        Item item = new Item()
                .withString("id", UUID.randomUUID().toString())
                .withString("vrm", vrm)
                .withString("email", email)
                .withString("mot_due_date", LocalDate.now().toString())
                .withString("mot_due_date_md", LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd")))
                .withString("created_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        dynamoDb.getTable(tableName).putItem(item);

        return item.get("id").toString();
    }
}
