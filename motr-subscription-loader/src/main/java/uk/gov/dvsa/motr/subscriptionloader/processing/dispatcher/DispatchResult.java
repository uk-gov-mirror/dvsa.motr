package uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher;

import com.amazonaws.services.sqs.model.SendMessageResult;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

import java.util.concurrent.Future;

/**
 * Wrapper for future message result to abstract from it and expose only data that are necessary
 */
public class DispatchResult {

    private Subscription originalPayload;
    private Future<SendMessageResult> result;
    private Exception exception;

    public DispatchResult(Subscription originalPayload, Future<SendMessageResult> futureResult) {

        this.originalPayload = originalPayload;
        this.result = futureResult;
    }

    public boolean isDone() {

        return result.isDone();
    }

    public boolean isFailed() {

        if (result.isDone()) {
            try {
                result.get();
            } catch (Exception execException) {
                this.exception = execException;
            }
        }
        return exception != null;
    }

    public Exception getError() {

        isFailed();
        return exception;
    }

    public Subscription getSubscription() {

        return originalPayload;
    }
}
