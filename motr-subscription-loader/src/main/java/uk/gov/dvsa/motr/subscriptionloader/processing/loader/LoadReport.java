package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

public class LoadReport {

    private int submittedForProcessing = 0;
    private int totalProcessed = 0;
    private int motDvlaVehiclesProcessed = 0;
    private int motNonDvlaVehiclesProcessed = 0;
    private int hgvVehiclesProcessed = 0;
    private int psvVehiclesProcessed = 0;
    private int hgvTrailersProcessed = 0;
    private int otherVehcilesProcessed = 0;
    private long startedTime = 0;

    public void incrementTotalProcessed() {

        totalProcessed++;
    }

    public void incrementDvlaVehiclesProcessed() {

        motDvlaVehiclesProcessed++;
    }

    public void incrementNonDvlaVehiclesProcessed() {

        motNonDvlaVehiclesProcessed++;
    }

    public void incrementHgvVehiclesProcessed() {
        hgvVehiclesProcessed++;
    }

    public void incrementPsvVehiclesProcessed() {
        psvVehiclesProcessed++;
    }

    public void incrementHgvTrailersProcessed() {
        hgvTrailersProcessed++;
    }

    public void incrementOtherVehiclesPorcessed() {
        otherVehcilesProcessed++;
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

    public int getMotDvlaVehiclesProcessed() {

        return motDvlaVehiclesProcessed;
    }

    public int getMotNonDvlaVehiclesProcessed() {

        return motNonDvlaVehiclesProcessed;
    }

    public int getHgvVehiclesProcessed() {
        return hgvVehiclesProcessed;
    }

    public int getPsvVehiclesProcessed() {
        return psvVehiclesProcessed;
    }

    public int getHgvTrailersProcessed() {
        return hgvTrailersProcessed;
    }

    public int getOtherVehcilesProcessed() {
        return otherVehcilesProcessed;
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
                ", motDvlaVehiclesProcessed=" + motDvlaVehiclesProcessed +
                ", motNonDvlaVehiclesProcessed=" + motNonDvlaVehiclesProcessed +
                ", hgvVehiclesProcessed=" + hgvVehiclesProcessed +
                ", psvVehiclesProcessed=" + psvVehiclesProcessed +
                ", hgvTrailersProcessed=" + hgvTrailersProcessed +
                ", startedTime=" + startedTime +
                '}';
    }
}
