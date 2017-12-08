package uk.gov.dvsa.motr.web.performance.warmup;

public interface LambdaWarmUp {

    LambdaWarmUp NOOP = () -> {
    };

    void warmUp();
}
