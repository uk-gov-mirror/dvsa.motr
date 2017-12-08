package uk.gov.dvsa.motr.report;

public class BouncingEmailCleanerReport {

    private int subscriptionsSuccessfullyCancelled = 0;

    private long cleanUpStartTime = 0;

    public BouncingEmailCleanerReport incrementSuccessfullyCancelled() {

        subscriptionsSuccessfullyCancelled += 1;

        return this;
    }

    public BouncingEmailCleanerReport startCleanUp() {

        cleanUpStartTime = System.currentTimeMillis();

        return this;
    }

    public long getDurationOfCleanUp() {

        return System.currentTimeMillis() - cleanUpStartTime;
    }

    public int getNumberOfSubscriptionsSuccessfullyCancelled() {

        return subscriptionsSuccessfullyCancelled;
    }
}
