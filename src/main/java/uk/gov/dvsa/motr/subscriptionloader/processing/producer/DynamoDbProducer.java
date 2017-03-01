package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * Produces
 */
public class DynamoDbProducer implements SubscriptionProducer {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDbProducer.class);
    private static final String INDEX_NAME = "due-date-md-gsi";

    private String subscriptionTableName;
    private DynamoDB dynamoDb;

    public DynamoDbProducer(DynamoDB dynamoDb, String subscriptionTableName) {

        this.dynamoDb = dynamoDb;
        this.subscriptionTableName = subscriptionTableName;
    }

    public Iterator<Subscription> getIterator(LocalDate firstNotificationDate, LocalDate secondNotificationDate) {

        Index dueDateIndex = dynamoDb.getTable(subscriptionTableName).getIndex(INDEX_NAME);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

        ValueMap valueMap = new ValueMap()
                .withString(":due_date1", firstNotificationDate.format(dateFormatter))
                .withString(":due_date2", secondNotificationDate.format(dateFormatter));

        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("mot_due_date_md = :due_date1 OR mot_due_date_md = :due_date2")
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> resultCollection = dueDateIndex.query(querySpec);

        Iterator<Item> iterator = resultCollection.iterator();

        return new Iterator<Subscription>() {

            @Override
            public boolean hasNext() {

                return iterator.hasNext();
            }

            @Override
            public Subscription next() {

                Item item = iterator.next();
                logger.debug("item is {}", item);
                LocalDate motDueDate = LocalDate.parse(item.getString("mot_due_date"), DateTimeFormatter.ISO_DATE);
                return new Subscription()
                        .setId(item.getString("id"))
                        .setVrm(item.getString("vrm"))
                        .setEmail(item.getString("email"))
                        .setMotDueDate(motDueDate);

            }
        };
    }
}
