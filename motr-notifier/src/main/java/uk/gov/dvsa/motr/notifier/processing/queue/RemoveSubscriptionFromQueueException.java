package uk.gov.dvsa.motr.notifier.processing.queue;

public class RemoveSubscriptionFromQueueException extends Exception {

    public RemoveSubscriptionFromQueueException(Exception exception) {

        super(exception);
    }
}
