package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

class LoadingException extends Exception {

    public LoadingException(Exception cause) {
        super(cause);
    }
}
