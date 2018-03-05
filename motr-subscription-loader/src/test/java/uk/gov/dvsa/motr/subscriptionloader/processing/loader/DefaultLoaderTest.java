package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.model.SendMessageResult;

import org.junit.Test;

import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.DispatchResult;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.ContactDetail;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class DefaultLoaderTest {

    private static final String TEST_TIME_STRING = "2011-01-01T12:12:12Z";

    private SubscriptionProducer producer = mock(SubscriptionProducer.class);
    private Dispatcher dispatcher = mock(Dispatcher.class);
    private Context context = mock(Context.class);

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithCorrectDateValues() throws Exception {

        Iterator mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(false);
        when(this.producer.getIterator(any(), any(), any())).thenReturn(mockIterator);

        DefaultLoader loader = new DefaultLoader(this.producer, this.dispatcher);
        loader.run(getTestLocalDate(), context);

        LocalDate oneMonth = getTestLocalDate().plusDays(30L);
        LocalDate twoWeeks = getTestLocalDate().plusDays(14L);
        LocalDate oneDayBehind = getTestLocalDate().minusDays(1L);

        verify(producer, times(1)).getIterator(eq(oneMonth), eq(twoWeeks), eq(oneDayBehind));
    }

    @Test
    public void whenThereAreDispatchedItems_thenTheResultsAreProcessedForTheReport() throws Exception {

        Iterator mockIterator = createIteratorWithHasNextSequence(true, true, false);
        when(this.producer.getIterator(any(), any(), any())).thenReturn(mockIterator);

        Subscription mockSubscription = new Subscription()
                .setContactDetail(new ContactDetail("email@email.com", Subscription.ContactType.EMAIL))
                .setId("someId")
                .setMotDueDate(LocalDate.now())
                .setVrm("aaa").setMotTestNumber("123456");
        when(mockIterator.next()).thenReturn(mockSubscription);

        when(this.dispatcher.dispatch(any())).thenReturn(
                new DispatchResult(mockSubscription, completedFuture(mock(SendMessageResult.class))));
        when(context.getRemainingTimeInMillis()).thenReturn(400000);

        DefaultLoader loader = new DefaultLoader(this.producer, this.dispatcher);
        LoadReport report = loader.run(getTestLocalDate(), context);

        verify(dispatcher, times(2)).dispatch(mockSubscription);
        assertEquals(2, report.getSubmittedForProcessing());
        assertEquals(2, report.getNonDvlaVehiclesProcessed());
    }

    @Test
    public void whenThereAreDispatchedItems_thenTheResultsAreProcessedForTheReportWithDvlaVehicles() throws Exception {

        Iterator mockIterator = createIteratorWithHasNextSequence(true, true, false);
        when(this.producer.getIterator(any(), any(), any())).thenReturn(mockIterator);

        Subscription mockSubscription = new Subscription()
                .setContactDetail(new ContactDetail("email@email.com", Subscription.ContactType.EMAIL))
                .setId("someId")
                .setMotDueDate(LocalDate.now())
                .setVrm("aaa")
                .setDvlaId("123456");
        when(mockIterator.next()).thenReturn(mockSubscription);

        when(this.dispatcher.dispatch(any())).thenReturn(
                new DispatchResult(mockSubscription, completedFuture(mock(SendMessageResult.class))));
        when(context.getRemainingTimeInMillis()).thenReturn(400000);

        DefaultLoader loader = new DefaultLoader(this.producer, this.dispatcher);
        LoadReport report = loader.run(getTestLocalDate(), context);

        verify(dispatcher, times(2)).dispatch(mockSubscription);
        assertEquals(2, report.getSubmittedForProcessing());
        assertEquals(2, report.getDvlaVehiclesProcessed());
        assertEquals(0, report.getNonDvlaVehiclesProcessed());
    }

    @Test(expected = LoadingException.class)
    public void whenThereIsAFailedDispatchResult_thenProcessingStops() throws Exception {

        Iterator mockIterator = createIteratorWithHasNextSequence(true, true, false);
        when(this.producer.getIterator(any(), any(), any())).thenReturn(mockIterator);

        Subscription mockSubscription = new Subscription();
        when(mockIterator.next()).thenReturn(mockSubscription);

        RuntimeException ex = new RuntimeException("test_loading_error");
        Future<SendMessageResult> future = mock(Future.class);
        when(future.get()).thenThrow(ex);
        when(future.isDone()).thenReturn(true);
        DispatchResult dispatchResult =
                new DispatchResult(mockSubscription, future);
        when(this.dispatcher.dispatch(any())).thenReturn(dispatchResult);

        when(context.getRemainingTimeInMillis()).thenReturn(400000);
        DefaultLoader loader = new DefaultLoader(this.producer, this.dispatcher);
        LoadReport report = loader.run(getTestLocalDate(), context);
    }

    private LocalDate getTestLocalDate() {
        return LocalDateTime.parse(TEST_TIME_STRING, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
    }

    private Iterator createIteratorWithHasNextSequence(boolean... hasNext) {
        Iterator mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(hasNext[0], hasNext[1], hasNext[2]);
        return mockIterator;
    }

}
