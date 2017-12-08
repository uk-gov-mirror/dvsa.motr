package uk.gov.dvsa.motr.performance;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

public class MetricsTimerWrapper {

    private static final double NANO_TO_MILLIS_FACTOR = 1000000.0;
    private Timer timer;
    private Snapshot snapshot;

    public MetricsTimerWrapper(Timer timer) {

        this.timer = timer;
        this.snapshot = timer.getSnapshot();
    }

    public long getCount() {

        return timer.getCount();
    }

    public double getMax() {

        double maxNano = snapshot.getMax();
        return maxNano / NANO_TO_MILLIS_FACTOR;
    }

    public double getMin() {

        double minNano = snapshot.getMin();
        return minNano / NANO_TO_MILLIS_FACTOR;
    }

    public double getMean() {

        return snapshot.getMean() / NANO_TO_MILLIS_FACTOR;
    }

    public double getStdDev() {

        return snapshot.getStdDev() / NANO_TO_MILLIS_FACTOR;
    }

    public double getMedian() {

        return snapshot.getMedian() / NANO_TO_MILLIS_FACTOR;
    }

    public double get75thPercentile() {

        return snapshot.get75thPercentile() / NANO_TO_MILLIS_FACTOR;
    }

    public double get95thPercentile() {

        return snapshot.get95thPercentile() / NANO_TO_MILLIS_FACTOR;
    }

    private double get98thPercentile() {

        return snapshot.get98thPercentile() / NANO_TO_MILLIS_FACTOR;
    }

    public double get99thPercentile() {

        return snapshot.get99thPercentile() / NANO_TO_MILLIS_FACTOR;
    }

    public double get999thPercentile() {

        return snapshot.get999thPercentile() / NANO_TO_MILLIS_FACTOR;
    }

    public double getTotalTimeMs() {

        return timer.getCount() * getMean();
    }
}
