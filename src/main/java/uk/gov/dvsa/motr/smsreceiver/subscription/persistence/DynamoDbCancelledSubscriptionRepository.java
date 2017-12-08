package uk.gov.dvsa.motr.smsreceiver.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import uk.gov.dvsa.motr.smsreceiver.subscription.model.CancelledSubscription;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import javax.inject.Inject;

public class DynamoDbCancelledSubscriptionRepository implements CancelledSubscriptionRepository {

    private static final int MONTHS_TO_DELETION = 59;

    private DynamoDB dynamoDb;
    private String tableName;

    @Inject
    public DynamoDbCancelledSubscriptionRepository(String tableName, String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    @Override
    public Iterator<Item> findCancelledSubscriptionByVrmAndMobile(String vrm, String mobileNumber) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("email = :mobile")
                .withValueMap(new ValueMap().withString(":mobile", mobileNumber));

        Table table = dynamoDb.getTable(tableName);

        ItemCollection<QueryOutcome> items = table.query(query);
        return items.iterator();
    }

    @Override
    public void save(CancelledSubscription cancelledSubscription) {

        Item item = new Item()
                .withString("id", cancelledSubscription.getUnsubscribeId())
                .withString("vrm", cancelledSubscription.getVrm())
                .withString("email", cancelledSubscription.getEmail())
                .withString("reason_for_cancellation", cancelledSubscription.getReasonForCancellation())
                .withString("cancelled_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .withNumber("deletion_date", ZonedDateTime.now().plusMonths(MONTHS_TO_DELETION).toEpochSecond());

        dynamoDb.getTable(tableName).putItem(item);
    }
}

