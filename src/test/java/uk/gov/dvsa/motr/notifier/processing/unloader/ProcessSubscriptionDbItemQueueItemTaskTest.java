package uk.gov.dvsa.motr.notifier.processing.unloader;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.queue.QueueItemRemover;
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
    private QueueItemRemover queueItemRemover = mock(QueueItemRemover.class);

    private ProcessSubscriptionTask processSubscriptionTask;

    @Before
    public void setUp() {

        subscriptionQueueItemToProcess = new SubscriptionQueueItem().setId("TEST-ID")
                .setMotDueDate(requestDate)
                .setVrm("12345")
                .setEmail("test@test.com");

        processSubscriptionTask = new ProcessSubscriptionTask(
                requestDate, subscriptionQueueItemToProcess, report, processSubscriptionService, queueItemRemover);
    }

    @Test
    public void subscriptionIsProcessedAndThenRemoved() throws Exception {

        doNothing().when(processSubscriptionService).processSubscription(any(), any());
        doNothing().when(queueItemRemover).removeProcessedQueueItem(any());

        processSubscriptionTask.run();

        verify(processSubscriptionService, times(1)).processSubscription(subscriptionQueueItemToProcess, requestDate);
        verify(queueItemRemover, times(1)).removeProcessedQueueItem(subscriptionQueueItemToProcess);
        verify(report, times(1)).incrementSuccessfullyProcessed();
    }

    @Test
    public void whenSubscriptionFailsThenReportIncrementsFailedToProcess() throws Exception {

        doThrow(VehicleNotFoundException.class).when(processSubscriptionService).processSubscription(any(), any());
        doNothing().when(queueItemRemover).removeProcessedQueueItem(any());

        processSubscriptionTask.run();

        verify(processSubscriptionService, times(1)).processSubscription(subscriptionQueueItemToProcess, requestDate);
        verify(queueItemRemover, times(0)).removeProcessedQueueItem(subscriptionQueueItemToProcess);
        verify(report, times(1)).incrementFailedToProcess();
        verify(report, times(0)).incrementSuccessfullyProcessed();
    }
}
