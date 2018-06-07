package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.web.component.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DB_TABLE_CANCELLED_SUBSCRIPTION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.REGION;

public class DynamoDbCancelledSubscriptionRepository implements CancelledSubscriptionRepository {

    private static final int HOURS_TO_DELETION = 24;

    private final DynamoDB dynamoDb;
    private final String tableName;

    @Inject
    public DynamoDbCancelledSubscriptionRepository(
            @SystemVariableParam(DB_TABLE_CANCELLED_SUBSCRIPTION) String tableName,
            @SystemVariableParam(REGION) String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    @Override
    public void save(CancelledSubscription cancelledSubscription) {

        Item item = new Item()
                .withString("id", cancelledSubscription.getUnsubscribeId())
                .withString("vrm", cancelledSubscription.getVrm())
                .withString("email", cancelledSubscription.getContactDetail().getValue())
                .withString("vehicle_type", cancelledSubscription.getVehicleType().toString())
                .withString("reason_for_cancellation", cancelledSubscription.getReasonForCancellation())
                .withString("cancelled_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .withNumber("deletion_date", ZonedDateTime.now().plusHours(HOURS_TO_DELETION).toEpochSecond());

        dynamoDb.getTable(tableName).putItem(item);
    }
}
