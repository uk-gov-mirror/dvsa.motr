package uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

/**
 * Wrapper for future message result to abstract from it and expose only data that are necessary
 */
public class DispatchResult implements AsyncHandler<SendMessageRequest, SendMessageResult> {

    private volatile Exception error;

    private volatile boolean isDone = false;

    private Subscription originalPayload;

    DispatchResult(Subscription originalPayload) {

        this.originalPayload = originalPayload;
    }

    public boolean isDone() {

        return isDone;
    }

    public boolean isFailed() {

        return this.error != null;
    }

    public Exception getError() {

        return this.error;
    }

    public Subscription getSubscription() {
        return originalPayload;
    }

    @Override
    public void onError(Exception exception) {

        isDone = true;
        this.error = exception;
    }

    @Override
    public void onSuccess(SendMessageRequest request, SendMessageResult sendMessageResult) {

        isDone = true;
    }
}
