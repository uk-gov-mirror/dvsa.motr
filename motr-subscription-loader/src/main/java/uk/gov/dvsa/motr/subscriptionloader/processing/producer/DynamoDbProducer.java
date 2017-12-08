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

    public Iterator<Subscription> getIterator(LocalDate date1MonthAheadDueDate, LocalDate date2WeeksAheadDueDate,
            LocalDate date1DayBehindDueDate) {

        Index dueDateIndex = dynamoDb.getTable(subscriptionTableName).getIndex(INDEX_NAME);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

        String keyCondExpr = "mot_due_date_md = :due_date";

        QuerySpec querySpec1MonthAhead = new QuerySpec()
                .withKeyConditionExpression(keyCondExpr)
                .withValueMap(new ValueMap().withString(":due_date", date1MonthAheadDueDate.format(dateFormatter)));

        QuerySpec querySpec2WeeksAhead = new QuerySpec()
                .withKeyConditionExpression(keyCondExpr)
                .withValueMap(new ValueMap().withString(":due_date", date2WeeksAheadDueDate.format(dateFormatter)));

        QuerySpec querySpec1DayBehind = new QuerySpec()
                .withKeyConditionExpression(keyCondExpr)
                .withValueMap(new ValueMap().withString(":due_date", date1DayBehindDueDate.format(dateFormatter)));


        ItemCollection<QueryOutcome> result2WeeksAhead = dueDateIndex.query(querySpec2WeeksAhead);
        ItemCollection<QueryOutcome> result1MonthAhead = dueDateIndex.query(querySpec1MonthAhead);
        ItemCollection<QueryOutcome> result1DayBehind = dueDateIndex.query(querySpec1DayBehind);

        Iterator<Item> iterator2WeeksAhead = result2WeeksAhead.iterator();
        Iterator<Item> iterator1MonthAhead = result1MonthAhead.iterator();
        Iterator<Item> iterator1DayBehind = result1DayBehind.iterator();

        return new Iterator<Subscription>() {

            @Override
            public boolean hasNext() {

                return iterator2WeeksAhead.hasNext() || iterator1MonthAhead.hasNext() || iterator1DayBehind.hasNext();
            }

            @Override
            public Subscription next() {

                Item item;
                if (iterator2WeeksAhead.hasNext()) {
                    item = iterator2WeeksAhead.next();
                } else if (iterator1MonthAhead.hasNext()) {
                    item = iterator1MonthAhead.next();
                } else {
                    item = iterator1DayBehind.next();
                }

                LocalDate motDueDate = LocalDate.parse(item.getString("mot_due_date"), DateTimeFormatter.ISO_DATE);
                return new Subscription()
                        .setId(item.getString("id"))
                        .setVrm(item.getString("vrm"))
                        .setEmail(item.getString("email"))
                        .setMotTestNumber(item.getString("mot_test_number"))
                        .setMotDueDate(motDueDate);

            }
        };
    }
}
