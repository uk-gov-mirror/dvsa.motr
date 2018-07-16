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

        this.loadReport.incrementTotalProcessed();
        assertEquals(1, loadReport.getTotalProcessed());
    }

    @Test
    public void whenIncrementSubmittedForProcessing_thenSubmittedForProcessingIsIncremented() {

        this.loadReport.incrementSubmittedForProcessing();
        assertEquals(1, loadReport.getSubmittedForProcessing());
    }

    @Test
    public void whenIncrementDvlaVehiclesProcessed_thenDvlaVehiclesProcessedIsIncremented() {

        this.loadReport.incrementDvlaVehiclesProcessed();
        assertEquals(1, loadReport.getMotDvlaVehiclesProcessed());
    }

    @Test
    public void whenIncrementNonDvlaVehiclesProcessed_thenNonDvlaVehiclesProcessedIsIncremented() {

        this.loadReport.incrementNonDvlaVehiclesProcessed();
        assertEquals(1, loadReport.getMotNonDvlaVehiclesProcessed());
    }

    @Test
    public void testToStringReflectsValuesCorrectly() {

        this.loadReport.incrementSubmittedForProcessing();
        this.loadReport.incrementSubmittedForProcessing();
        this.loadReport.incrementTotalProcessed();
        this.loadReport.incrementDvlaVehiclesProcessed();
        this.loadReport.incrementNonDvlaVehiclesProcessed();

        assertTrue(loadReport.toString().contains("LoadReport{submittedForProcessing=2, totalProcessed=1, " +
                "motDvlaVehiclesProcessed=1, motNonDvlaVehiclesProcessed=1, " +
                "hgvVehiclesProcessed=0, psvVehiclesProcessed=0, hgvTrailersProcessed=0, startedTime="));
    }
}
