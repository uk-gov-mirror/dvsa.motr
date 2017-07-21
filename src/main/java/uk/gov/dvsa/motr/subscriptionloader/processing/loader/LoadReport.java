package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

public class LoadReport {

    private int submittedForProcessing = 0;
    private int totalProcessed = 0;
    private int dvlaVehiclesProcessed = 0;
    private int nonDvlaVehiclesProcessed = 0;
    private long startedTime = 0;

    public void incrementTotalProcessed() {

        totalProcessed++;
    }

    public void incrementDvlaVehiclesProcessed() {

        dvlaVehiclesProcessed++;
    }

    public void incrementNonDvlaVehiclesProcessed() {

        nonDvlaVehiclesProcessed++;
    }

    public void incrementSubmittedForProcessing() {

        submittedForProcessing++;
    }

    public int getTotalProcessed() {

        return totalProcessed;
    }

    public int getSubmittedForProcessing() {

        return submittedForProcessing;
    }

    public int getDvlaVehiclesProcessed() {

        return dvlaVehiclesProcessed;
    }

    public int getNonDvlaVehiclesProcessed() {

        return nonDvlaVehiclesProcessed;
    }

    public void startProcessing() {

        startedTime = System.currentTimeMillis();
    }

    public long getDuration() {

        return System.currentTimeMillis() - startedTime;
    }

    @Override
    public String toString() {
        return "LoadReport{" +
                "submittedForProcessing=" + submittedForProcessing +
                ", totalProcessed=" + totalProcessed +
                ", dvlaVehiclesProcessed=" + dvlaVehiclesProcessed +
                ", nonDvlaVehiclesProcessed=" + nonDvlaVehiclesProcessed +
                ", startedTime=" + startedTime +
                '}';
    }
}
