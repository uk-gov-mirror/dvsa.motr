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

    private static final LocalDate TEST_DATE_STANDARD_MONTH = LocalDate.of(2011, 1, 1);
    private static final LocalDate TEST_DATE_LEAP_YEAR = LocalDate.of(2016, 1, 29);
    private static final LocalDate TEST_DATE_NOT_PRESERVATION_DATE = LocalDate.of(2017, 1, 31);
    private static final LocalDate TEST_DATE_END_OF_MARCH = LocalDate.of(2017, 2, 28);
    private static final LocalDate TEST_DATE_SHORT_MONTH = LocalDate.of(2017, 4, 30);

    @Mock
    private SubscriptionProducer producer;

    @Mock
    private Dispatcher dispatcher;

    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor<List<SubscriptionCriteria>> criteriaCaptor;

    private DefaultLoader loader;

    private Boolean hgvPsvSubscriptionLoader = true;


    @Before
    public void setup() {
        initMocks(this);
        when(context.getRemainingTimeInMillis()).thenReturn(400000);
        loader = new DefaultLoader(producer, dispatcher, hgvPsvSubscriptionLoader);
    }

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithCorrectDateValuesForTwoWeekNotifications() throws Exception {
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE_STANDARD_MONTH, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(9, criteria.size());
        assertThat(criteria, containsSubscriptionTwoWeek(VehicleType.MOT, 14));
        assertThat(criteria, containsSubscriptionTwoWeek(VehicleType.MOT, -1));
        assertThat(criteria, containsSubscriptionTwoWeek(VehicleType.HGV, 60));
        assertThat(criteria, containsSubscriptionTwoWeek(VehicleType.PSV, 60));
        assertThat(criteria, containsSubscriptionTwoWeek(VehicleType.TRAILER, 60));
    }

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithCorrectDateValuesOneMonth() throws Exception {
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE_STANDARD_MONTH, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(9, criteria.size());
        assertThat(criteria, containsSubscriptionOneMonth(VehicleType.MOT, 1));
        assertThat(criteria, containsSubscriptionOneMonth(VehicleType.HGV, 1));
        assertThat(criteria, containsSubscriptionOneMonth(VehicleType.PSV, 1));
        assertThat(criteria, containsSubscriptionOneMonth(VehicleType.TRAILER, 1));
    }

    @Test
    public void whenRunCalledWithLocalDate_AndHgvSubscriptionsTurnedOff_thenProducerCalledWithCorrectDateValuesOneMonth() throws Exception {
        loader = new DefaultLoader(producer, dispatcher, false);
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE_STANDARD_MONTH, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(3, criteria.size());
        assertThat(criteria, containsSubscriptionOneMonth(VehicleType.MOT, 1));
        assertThat(criteria, doesNotContainSubscriptionOneMonth(VehicleType.HGV, 1));
        assertThat(criteria, doesNotContainSubscriptionOneMonth(VehicleType.PSV, 1));
        assertThat(criteria, doesNotContainSubscriptionOneMonth(VehicleType.TRAILER, 1));
    }

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithCorrectDateValuesOneMonthLeapYear() throws Exception {
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE_LEAP_YEAR, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(9, criteria.size());
        assertThat(criteria, containsSubscriptionOneMonthLeapYear(VehicleType.MOT, 1));
        assertThat(criteria, containsSubscriptionOneMonthLeapYear(VehicleType.HGV, 1));
        assertThat(criteria, containsSubscriptionOneMonthLeapYear(VehicleType.PSV, 1));
        assertThat(criteria, containsSubscriptionOneMonthLeapYear(VehicleType.TRAILER, 1));
    }

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithCorrectDateValuesOneMonthEndOfMarch() throws Exception {
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE_END_OF_MARCH, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(21, criteria.size());
        assertThat(criteria, containsSubscriptionOneMonthEndOfMarch(VehicleType.MOT, 1));
        assertThat(criteria, containsSubscriptionOneMonthEndOfMarch(VehicleType.HGV, 1));
        assertThat(criteria, containsSubscriptionOneMonthEndOfMarch(VehicleType.PSV, 1));
        assertThat(criteria, containsSubscriptionOneMonthEndOfMarch(VehicleType.TRAILER, 1));
    }

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithCorrectDateValuesShortMonth() throws Exception {
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE_SHORT_MONTH, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(13, criteria.size());
        assertThat(criteria, containsSubscriptionOneMonthShortMonth(VehicleType.MOT, 1));
        assertThat(criteria, containsSubscriptionOneMonthShortMonth(VehicleType.HGV, 1));
        assertThat(criteria, containsSubscriptionOneMonthShortMonth(VehicleType.PSV, 1));
        assertThat(criteria, containsSubscriptionOneMonthShortMonth(VehicleType.TRAILER, 1));
    }

    @Test
    public void whenRunCalledWithLocalDate_thenProducerCalledWithoutOneMonthValuesForNonPreservationDate() throws Exception {
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(Collections.emptyIterator());

        loader.run(TEST_DATE_NOT_PRESERVATION_DATE, context);

        verify(producer, times(1))
                .searchSubscriptions(criteriaCaptor.capture());
        List<SubscriptionCriteria> criteria = criteriaCaptor.getValue();
        assertEquals(5, criteria.size());
        assertThat(criteria, doesNotcontainSubscriptionOneMonthNonPreservationDate(VehicleType.MOT, 1));
        assertThat(criteria, doesNotcontainSubscriptionOneMonthNonPreservationDate(VehicleType.HGV, 1));
        assertThat(criteria, doesNotcontainSubscriptionOneMonthNonPreservationDate(VehicleType.PSV, 1));
        assertThat(criteria, doesNotcontainSubscriptionOneMonthNonPreservationDate(VehicleType.TRAILER, 1));
    }

    @Test
    public void whenThereAreDispatchedItems_thenTheResultsAreProcessedForTheReport() throws Exception {
        Subscription subscription = getTestSubscription().setMotTestNumber("123456");
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(createIterator(subscription, 2));
        when(this.dispatcher.dispatch(any()))
                .thenReturn(createDispatchResult(subscription));

        LoadReport report = loader.run(TEST_DATE_STANDARD_MONTH, context);

        verify(dispatcher, times(2)).dispatch(subscription);
        assertEquals(2, report.getSubmittedForProcessing());
        assertEquals(2, report.getMotNonDvlaVehiclesProcessed());

    }

    @Test
    public void whenThereAreDispatchedItems_thenTheResultsAreProcessedForTheReportWithDvlaVehicles() throws Exception {
        Subscription subscription = getTestSubscription()
                .setDvlaId("123456");;
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(createIterator(subscription, 3));
        when(this.dispatcher.dispatch(any()))
                .thenReturn(createDispatchResult(subscription));

        LoadReport report = loader.run(TEST_DATE_STANDARD_MONTH, context);

        verify(dispatcher, times(3)).dispatch(subscription);
        assertEquals(3, report.getSubmittedForProcessing());
        assertEquals(3, report.getMotDvlaVehiclesProcessed());
        assertEquals(0, report.getMotNonDvlaVehiclesProcessed());
        assertEquals(0, report.getHgvVehiclesProcessed());
        assertEquals(0, report.getPsvVehiclesProcessed());
        assertEquals(0, report.getHgvTrailersProcessed());
    }

    @Test
    public void whenThereAreDispatchedItems_thenTheResultsAreProcessedForTheReportWithHgvTrailers() throws Exception {
        Subscription subscription = getTestSubscription()
                .setVehicleType(VehicleType.TRAILER);
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(createIterator(subscription, 3));
        when(this.dispatcher.dispatch(any()))
                .thenReturn(createDispatchResult(subscription));

        LoadReport report = loader.run(TEST_DATE_STANDARD_MONTH, context);

        verify(dispatcher, times(3)).dispatch(subscription);
        assertEquals(3, report.getSubmittedForProcessing());
        assertEquals(0, report.getMotDvlaVehiclesProcessed());
        assertEquals(0, report.getMotNonDvlaVehiclesProcessed());
        assertEquals(0, report.getHgvVehiclesProcessed());
        assertEquals(0, report.getPsvVehiclesProcessed());
        assertEquals(3, report.getHgvTrailersProcessed());
    }

    @Test(expected = LoadingException.class)
    public void whenThereIsAFailedDispatchResult_thenProcessingStops() throws Exception {
        Subscription subscription = new Subscription();
        when(this.producer.searchSubscriptions(any()))
                .thenReturn(createIterator(subscription, 2));
        when(this.dispatcher.dispatch(any()))
                .thenReturn(createExceptionalDispatchResult(subscription));

        loader.run(TEST_DATE_STANDARD_MONTH, context);
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

    private Matcher<List<SubscriptionCriteria>> containsSubscriptionTwoWeek(final VehicleType vehicleType, final int inDays) {
        final LocalDate testDueDate = TEST_DATE_STANDARD_MONTH.plusDays(inDays);
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

    private Matcher<List<SubscriptionCriteria>> containsSubscriptionOneMonth(final VehicleType vehicleType, final int inMonths) {
        final LocalDate testDueDate = TEST_DATE_STANDARD_MONTH.plusMonths(inMonths);
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

    private Matcher<List<SubscriptionCriteria>> doesNotContainSubscriptionOneMonth(
            final VehicleType vehicleType,
            final int inMonths) {
        final LocalDate testDueDate = TEST_DATE_STANDARD_MONTH.plusMonths(inMonths);
        return new TypeSafeMatcher<List<SubscriptionCriteria>>() {
            @Override
            protected boolean matchesSafely(List<SubscriptionCriteria> item) {
                return !(item.stream().anyMatch(criteria ->
                        Objects.equals(criteria.getVehicleType(), vehicleType)
                                && Objects.equals(criteria.getTestDueDate(), testDueDate)
                ));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("list should contain subscription with ")
                        .appendText("vehicleType = ").appendValue(vehicleType).appendText(", ")
                        .appendText("testDueDate = ").appendValue(testDueDate).appendText(";");
            }
        };
    }

    private Matcher<List<SubscriptionCriteria>> containsSubscriptionOneMonthLeapYear(final VehicleType vehicleType, final int inMonths) {
        final LocalDate testDueDate = TEST_DATE_LEAP_YEAR.plusMonths(inMonths);
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

    private Matcher<List<SubscriptionCriteria>> containsSubscriptionOneMonthEndOfMarch(final VehicleType vehicleType, final int inMonths) {
        final LocalDate testDueDate = TEST_DATE_END_OF_MARCH.plusMonths(inMonths);
        final LocalDate testDueDate29Days = TEST_DATE_END_OF_MARCH.plusDays(29);
        final LocalDate testDueDate30Days = TEST_DATE_END_OF_MARCH.plusDays(30);
        final LocalDate testDueDate31Days = TEST_DATE_END_OF_MARCH.plusDays(31);

        return new TypeSafeMatcher<List<SubscriptionCriteria>>() {
            @Override
            protected boolean matchesSafely(List<SubscriptionCriteria> item) {
                return item.stream().anyMatch(criteria ->
                        Objects.equals(criteria.getVehicleType(), vehicleType)
                                && Objects.equals(criteria.getTestDueDate(), testDueDate)
                                || Objects.equals(criteria.getTestDueDate(), testDueDate29Days)
                                || Objects.equals(criteria.getTestDueDate(), testDueDate30Days)
                                || Objects.equals(criteria.getTestDueDate(), testDueDate31Days)
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

    private Matcher<List<SubscriptionCriteria>> containsSubscriptionOneMonthShortMonth(final VehicleType vehicleType, final int inMonths) {
        final LocalDate testDueDate = TEST_DATE_SHORT_MONTH.plusMonths(inMonths);
        final LocalDate testDueDate31Days = TEST_DATE_SHORT_MONTH.plusDays(31);

        return new TypeSafeMatcher<List<SubscriptionCriteria>>() {
            @Override
            protected boolean matchesSafely(List<SubscriptionCriteria> item) {
                return item.stream().anyMatch(criteria ->
                        Objects.equals(criteria.getVehicleType(), vehicleType)
                                && Objects.equals(criteria.getTestDueDate(), testDueDate)
                                || Objects.equals(criteria.getTestDueDate(), testDueDate31Days)
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

    private Matcher<List<SubscriptionCriteria>> doesNotcontainSubscriptionOneMonthNonPreservationDate(
            final VehicleType vehicleType,
            final int inMonths) {
        final LocalDate testDueDate = TEST_DATE_NOT_PRESERVATION_DATE.plusMonths(inMonths);
        return new TypeSafeMatcher<List<SubscriptionCriteria>>() {
            @Override
            protected boolean matchesSafely(List<SubscriptionCriteria> item) {
                return !(item.stream().anyMatch(criteria ->
                        Objects.equals(criteria.getVehicleType(), vehicleType)
                                && Objects.equals(criteria.getTestDueDate(), testDueDate)
                ));
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
