package uk.gov.dvsa.motr.notifier.processing.unloader;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.ContactDetail;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService;
import uk.gov.dvsa.motr.notifier.processing.service.VehicleNotFoundException;

import java.time.LocalDate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ProcessSubscriptionDbItemQueueItemTaskTest {

    private static LocalDate requestDate = LocalDate.of(2017, 10, 10);
    private SubscriptionQueueItem subscriptionQueueItemToProcess;
    private NotifierReport report = mock(NotifierReport.class);
    private ProcessSubscriptionService processSubscriptionService = mock(ProcessSubscriptionService.class);

    private ProcessSubscriptionTask processSubscriptionTask;

    // @todo Move tests to more relevant location (no longer associated with the unloader

    @Before
    public void setUp() {

        subscriptionQueueItemToProcess = new SubscriptionQueueItem().setId("TEST-ID")
                .setMotDueDate(requestDate)
                .setVrm("12345")
                .setMotTestNumber("test-mot-number-123")
                .setContactDetail(new ContactDetail("test@test.com", SubscriptionQueueItem.ContactType.EMAIL));

        processSubscriptionTask = new ProcessSubscriptionTask(processSubscriptionService);
    }

    @Test
    public void subscriptionIsProcessedAndThenRemoved() throws Exception {

        doNothing().when(processSubscriptionService).processSubscription(any());

        processSubscriptionTask.run(subscriptionQueueItemToProcess);

        verify(processSubscriptionService, times(1)).processSubscription(subscriptionQueueItemToProcess);
    }

    @Test(expected = Exception.class)
    public void whenSubscriptionFailsThenExceptionIsThrown() throws Exception {

        doThrow(VehicleNotFoundException.class).when(processSubscriptionService).processSubscription(any());

        processSubscriptionTask.run(subscriptionQueueItemToProcess);

        verify(processSubscriptionService, times(1)).processSubscription(subscriptionQueueItemToProcess);
    }
}
