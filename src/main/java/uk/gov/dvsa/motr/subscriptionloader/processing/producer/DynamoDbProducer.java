package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

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

        String first = firstNotificationDate.format(DateTimeFormatter.ofPattern("MM-dd"));
        String second = secondNotificationDate.format(DateTimeFormatter.ofPattern("MM-dd"));
        logger.debug("first scan date is {} and second scan is {}", first, second);
        
        ScanFilter firstDateScanFilter = new ScanFilter("mot_due_date_md")
                .eq(firstNotificationDate.format(DateTimeFormatter.ofPattern("MM-dd")));
        ScanFilter secondDateScanFilter = new ScanFilter("mot_due_date_md")
                .eq(secondNotificationDate.format(DateTimeFormatter.ofPattern("MM-dd")));

        ItemCollection<ScanOutcome> firstNotificationCollection = dueDateIndex.scan(firstDateScanFilter);
        ItemCollection<ScanOutcome> secondNotificationCollection = dueDateIndex.scan(secondDateScanFilter);

        Iterator<Item> firstNotificationsIterator = firstNotificationCollection.iterator();
        Iterator<Item> secondNotificationsIterator = secondNotificationCollection.iterator();

        return new Iterator<Subscription>() {

            @Override
            public boolean hasNext() {

                return firstNotificationsIterator.hasNext() || secondNotificationsIterator.hasNext();
            }

            @Override
            public Subscription next() {

                Item item;
                if (firstNotificationsIterator.hasNext()) {
                    item = firstNotificationsIterator.next();
                } else {
                    item = secondNotificationsIterator.next();
                }
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
