package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.model.SendMessageResult;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.DispatchResult;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.ContactDetail;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionCriteria;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class DefaultLoaderTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2011, 1, 1);

    @Mock
    private SubscriptionProducer producer;

    @Mock
    private Dispatcher dispatcher;

    @Mock
    private Context context;

    @InjectMocks
    private DefaultLoader loader;

    @Captor
    private ArgumentCaptor<List<SubscriptionCriteria>> criteriaCaptor;

    @Before
    public void setup() {
        initMocks(this);
        when(context.getRemainingTimeInMillis()).thenReturn(400000);
    }

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithCorrectDateValues() throws Exception {
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(7, criteria.size());
        assertThat(criteria, containsSubscription(VehicleType.MOT, 30));
        assertThat(criteria, containsSubscription(VehicleType.MOT, 14));
        assertThat(criteria, containsSubscription(VehicleType.MOT, -1));
        assertThat(criteria, containsSubscription(VehicleType.HGV, 30));
        assertThat(criteria, containsSubscription(VehicleType.HGV, 60));
        assertThat(criteria, containsSubscription(VehicleType.PSV, 30));
        assertThat(criteria, containsSubscription(VehicleType.PSV, 60));
    }

    @Test
    public void whenThereAreDispatchedItems_thenTheResultsAreProcessedForTheReport() throws Exception {
        Subscription subscription = getTestSubscription().setMotTestNumber("123456");
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(createIterator(subscription, 2));
        when(this.dispatcher.dispatch(any()))
                .thenReturn(createDispatchResult(subscription));

        LoadReport report = loader.run(TEST_DATE, context);

        verify(dispatcher, times(2)).dispatch(subscription);
        assertEquals(2, report.getSubmittedForProcessing());
        assertEquals(2, report.getNonDvlaVehiclesProcessed());
    }

    @Test
    public void whenThereAreDispatchedItems_thenTheResultsAreProcessedForTheReportWithDvlaVehicles() throws Exception {
        Subscription subscription = getTestSubscription().setDvlaId("123456");;
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(createIterator(subscription, 3));
        when(this.dispatcher.dispatch(any()))
                .thenReturn(createDispatchResult(subscription));

        LoadReport report = loader.run(TEST_DATE, context);

        verify(dispatcher, times(3)).dispatch(subscription);
        assertEquals(3, report.getSubmittedForProcessing());
        assertEquals(3, report.getDvlaVehiclesProcessed());
        assertEquals(0, report.getNonDvlaVehiclesProcessed());
    }

    @Test(expected = LoadingException.class)
    public void whenThereIsAFailedDispatchResult_thenProcessingStops() throws Exception {
        Subscription subscription = new Subscription();
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(createIterator(subscription, 2));
        when(this.dispatcher.dispatch(any()))
                .thenReturn(createExceptionalDispatchResult(subscription));

        loader.run(TEST_DATE, context);
    }

    private <T> Iterator<T> createIterator(T item, int numberOfItems) {
        List<T> subscriptions = new ArrayList<>(numberOfItems);
        for (int i = 0; i < numberOfItems; i++) {
            subscriptions.add(item);
        }
        return subscriptions.iterator();
    }

    private Subscription getTestSubscription() {
        return new Subscription()
                .setContactDetail(new ContactDetail("email@email.com", Subscription.ContactType.EMAIL))
                .setId("someId")
                .setMotDueDate(LocalDate.now())
                .setVrm("aaa")
                .setVehicleType(VehicleType.MOT);
    }

    private Matcher<List<SubscriptionCriteria>> containsSubscription(final VehicleType vehicleType, final int inDays) {
        final LocalDate testDueDate = TEST_DATE.plusDays(inDays);
        return new TypeSafeMatcher<List<SubscriptionCriteria>>() {
            @Override
            protected boolean matchesSafely(List<SubscriptionCriteria> item) {
                return item.stream().anyMatch(criteria ->
                        Objects.equals(criteria.getVehicleType(), vehicleType)
                                && Objects.equals(criteria.getTestDueDate(), testDueDate)
                );
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("list should contain subscription with ")
                        .appendText("vehicleType = ").appendValue(vehicleType).appendText(", ")
                        .appendText("testDueDate = ").appendValue(testDueDate).appendText(";");
            }
        };
    }

    private DispatchResult createDispatchResult(Subscription subscription) {
        return new DispatchResult(subscription, completedFuture(mock(SendMessageResult.class)));
    }

    private DispatchResult createExceptionalDispatchResult(Subscription subscription) {
        RuntimeException ex = new RuntimeException("test_loading_error");
        CompletableFuture<SendMessageResult> future = new CompletableFuture<>();
        future.completeExceptionally(ex);
        return new DispatchResult(subscription, future);
    }
}
