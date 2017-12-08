package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertEquals;

public class LoadReportTest {

    private LoadReport loadReport;

    @Before
    public void setup() {

        this.loadReport = new LoadReport();
    }

    @Test
    public void whenIncrementProcessed_thenProcessedIsIncremented() {

        this.loadReport.incrementProcessed();
        assertEquals(1, loadReport.getProcessed());
    }

    @Test
    public void whenIncrementSubmittedForProcessing_thenSubmittedForProcessingIsIncremented() {

        this.loadReport.incrementSubmittedForProcessing();
        assertEquals(1, loadReport.getSubmittedForProcessing());
    }

    @Test
    public void testToStringReflectsValuesCoeecrtly() {

        this.loadReport.incrementProcessed();
        this.loadReport.incrementSubmittedForProcessing();
        assertTrue(loadReport.toString().contains("LoadReport{submittedForProcessing=1, processed=1, startedTime="));
    }
}
