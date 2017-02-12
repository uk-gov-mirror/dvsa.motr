package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

class LoadReport {

    private int submittedForProcessing = 0;

    private int processed = 0;

    private long startedTime = 0;

    public void incrementProcessed() {
        processed++;
    }

    public void incrementSubmittedForProcessing() {
        submittedForProcessing++;
    }

    public int getProcessed() {
        return processed;
    }

    public int getSubmittedForProcessing() {
        return submittedForProcessing;
    }

    public void startProcessing() {
        startedTime = System.currentTimeMillis();
    }

    public long getDuration() {
        return System.currentTimeMillis() - startedTime;
    }
}
