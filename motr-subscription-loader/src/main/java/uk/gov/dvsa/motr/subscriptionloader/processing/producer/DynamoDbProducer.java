package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.ContactDetail;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Produces
 */
public class DynamoDbProducer implements SubscriptionProducer {

    private static final String INDEX_NAME = "due-date-md-gsi";
    private static final String DUE_DATE_INDEX_NAME = "due-date-md-vehicle-type-gsi";
    private static final String DUE_DATE_AND_VEHICLE_TYPE_EXPRESSION = "mot_due_date_md = :due_date and vehicle_type = :vehicle_type";

    private String subscriptionTableName;
    private DynamoDB dynamoDb;
    private DateTimeFormatter dayMonthFormatter = DateTimeFormatter.ofPattern("MM-dd");

    public DynamoDbProducer(DynamoDB dynamoDb, String subscriptionTableName) {

        this.dynamoDb = dynamoDb;
        this.subscriptionTableName = subscriptionTableName;
    }

    public Iterator<Subscription> searchSubscriptions(LocalDate date1MonthAheadDueDate, LocalDate date2WeeksAheadDueDate,
                                                      LocalDate date1DayBehindDueDate) {

        Index dueDateIndex = dynamoDb.getTable(subscriptionTableName).getIndex(INDEX_NAME);

        String keyCondExpr = "mot_due_date_md = :due_date";

        QuerySpec querySpec1MonthAhead = new QuerySpec()
                .withKeyConditionExpression(keyCondExpr)
                .withValueMap(new ValueMap().withString(":due_date", date1MonthAheadDueDate.format(dayMonthFormatter)));

        QuerySpec querySpec2WeeksAhead = new QuerySpec()
                .withKeyConditionExpression(keyCondExpr)
                .withValueMap(new ValueMap().withString(":due_date", date2WeeksAheadDueDate.format(dayMonthFormatter)));

        QuerySpec querySpec1DayBehind = new QuerySpec()
                .withKeyConditionExpression(keyCondExpr)
                .withValueMap(new ValueMap().withString(":due_date", date1DayBehindDueDate.format(dayMonthFormatter)));

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

                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                Item item;
                if (iterator2WeeksAhead.hasNext()) {
                    item = iterator2WeeksAhead.next();
                } else if (iterator1MonthAhead.hasNext()) {
                    item = iterator1MonthAhead.next();
                } else {
                    item = iterator1DayBehind.next();
                }

                return mapItemToSubscription(item);
            }
        };
    }

    public Iterator<Subscription> searchSubscriptions(List<SubscriptionCriteria> criteriaList) {
        Index dueDateIndex = dynamoDb.getTable(subscriptionTableName).getIndex(DUE_DATE_INDEX_NAME);

        final List<Iterator<Item>> outcomeIterators = criteriaList.stream().map(
                criteria -> searchSubscriptionsInIndex(dueDateIndex, criteria).iterator()
        ).collect(Collectors.toList());

        return new Iterator<Subscription>() {
            @Override
            public boolean hasNext() {
                return outcomeIterators.stream().anyMatch(Iterator::hasNext);
            }

            @Override
            public Subscription next() {
                Item item = outcomeIterators.stream()
                        .filter(Iterator::hasNext)
                        .findFirst()
                        .map(Iterator::next)
                        .orElseThrow(NoSuchElementException::new);

                return mapItemToSubscription(item);
            }
        };
    }

    private ItemCollection<QueryOutcome> searchSubscriptionsInIndex(Index dueDateIndex, SubscriptionCriteria criteria) {
        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression(DUE_DATE_AND_VEHICLE_TYPE_EXPRESSION)
                .withValueMap(
                        new ValueMap()
                                .withString(":due_date", criteria.getTestDueDate().format(dayMonthFormatter))
                                .withString("vehcle_type", criteria.getVehicleType().name())

                );
        return dueDateIndex.query(query);
    }

    private Subscription mapItemToSubscription(Item item) {
        LocalDate motDueDate = LocalDate.parse(item.getString("mot_due_date"), DateTimeFormatter.ISO_DATE);
        ContactDetail contactDetail = new ContactDetail(
                item.getString("email"),
                Subscription.ContactType.valueOf(item.getString("contact_type"))
        );

        return new Subscription()
                .setId(item.getString("id"))
                .setVrm(item.getString("vrm"))
                .setContactDetail(contactDetail)
                .setMotTestNumber(item.getString("mot_test_number"))
                .setDvlaId(item.getString("dvla_id"))
                .setMotDueDate(motDueDate)
                .setVehicleType(VehicleType.getFromString(item.getString("vehicle_type")));
    }
}
