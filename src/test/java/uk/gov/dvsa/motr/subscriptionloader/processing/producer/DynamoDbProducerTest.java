package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DynamoDbProducerTest {

    private static final String SUBSCRIPTION_TABLE_NAME = "sub_table_name";
    public static final String INDEX_NAME = "due-date-md-gsi";
    private DynamoDbProducer dynamoDbProducer;
    private DynamoDB dynamoDb = mock(DynamoDB.class);
    private Table table = mock(Table.class);
    private Index index = mock(Index.class);
    private LocalDate testFirstNotificationDate;
    private LocalDate testSecondNotificationDate;

    @Before
    public void setup() {

        this.dynamoDbProducer = new DynamoDbProducer(dynamoDb, SUBSCRIPTION_TABLE_NAME);
        this.testFirstNotificationDate = LocalDate.of(2016, 10, 20);
        this.testSecondNotificationDate = LocalDate.of(2016, 11, 12);
    }

    @Test
    public void testCorrectTableAndIndexAreInvokedWhenReadingItemsFromDb() {

        mockUpTable();
        mockUpIndex();

        ItemCollection<ScanOutcome> notifications = mock(ItemCollection.class);
        when(this.index.scan(any(ScanFilter.class))).thenReturn(notifications);

        this.dynamoDbProducer.getIterator(testFirstNotificationDate, testSecondNotificationDate);

        verify(this.dynamoDb, times(1)).getTable(SUBSCRIPTION_TABLE_NAME);
        verify(this.table, times(1)).getIndex(INDEX_NAME);
        verify(index, times(2)).scan(any(ScanFilter.class));
    }

    @Test
    public void whenAnItemIsRetrievedFromDb_thenASubscriptionItemIsReturned() {

        mockUpTable();
        mockUpIndex();

        ItemCollection<ScanOutcome> notifications = mock(ItemCollection.class);
        when(this.index.scan(any(ScanFilter.class))).thenReturn(notifications);

        IteratorSupport notificationsIterator = mock(IteratorSupport.class);
        when(notifications.iterator()).thenReturn(notificationsIterator);

        this.dynamoDbProducer.getIterator(testFirstNotificationDate, testSecondNotificationDate);
    }

    private void mockUpTable() {
        when(this.dynamoDb.getTable(any())).thenReturn(this.table);
    }

    private void mockUpIndex() {
        when(this.table.getIndex(INDEX_NAME)).thenReturn(this.index);
    }

}
