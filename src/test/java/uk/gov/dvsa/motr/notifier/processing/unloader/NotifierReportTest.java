package uk.gov.dvsa.motr.notifier.processing.unloader;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertEquals;

public class NotifierReportTest {

    private NotifierReport notifierReport;

    @Before
    public void setup() {

        this.notifierReport = new NotifierReport();
    }

    @Test
    public void whenIncrementProcessed_thenProcessedIsIncremented() {

        notifierReport.incrementSuccessfullyProcessed();
        assertEquals(1, notifierReport.getSuccessfullyProcessed());
    }

    @Test
    public void whenIncrementFailedToProcess_thenFailedToProcessIsIncremented() {

        notifierReport.incrementFailedToProcess();
        assertEquals(1, notifierReport.getFailedToProcess());
    }

    @Test
    public void whenStartProcessing_thenDurationIsCalculatedCorrectly() {

        notifierReport.startProcessingTheMessages();
        assertTrue(notifierReport.getDurationToProcessTheMessages() >= 0);
    }
}
