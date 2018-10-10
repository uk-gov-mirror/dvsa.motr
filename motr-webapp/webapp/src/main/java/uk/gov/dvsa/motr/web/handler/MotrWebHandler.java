package uk.gov.dvsa.motr.web.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.PingEvent;
import uk.gov.dvsa.motr.web.performance.ColdStartMarker;
import uk.gov.dvsa.motr.web.performance.warmup.LambdaWarmUp;
import uk.gov.dvsa.motr.web.performance.warmup.PingAwareAwsProxyRequest;
import uk.gov.dvsa.motr.web.system.MotrWebApplication;

/**
 * Entry point for Lambda
 */
public class MotrWebHandler {

    private static final Logger logger = LoggerFactory.getLogger(MotrWebHandler.class);

    protected MotrWebApplication application;
    private static JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    /**
     * Executes once per container instance
     */
    public MotrWebHandler() {
        logger.info("MotrWebHandler - konstruktor");
        application = new MotrWebApplication();
        logger.info("MotrWebHandler - konstruktor po utworzeniu aplikacji");
    }

    /**
     * Executes upon request. Request can either be PING request coming from Cloudwatch Events or HTTP request
     * proxies through API Gateway
     *
     * @param request request envelope
     * @param context lambda invocation context
     * @return null in the case of Ping request, AwsProxyResponse for HTTP requests
     */
    public AwsProxyResponse handleRequest(PingAwareAwsProxyRequest request, Context context) {

        try {
            logger.info("MotrWebHandler - handleRequest");
            if (handler == null) {
                handler = JerseyLambdaContainerHandler.getAwsProxyHandler(application);
            }
            logger.info("MotrWebHandler - handleRequest po utworzeniu handlera");

            if (request.isPing()) {
                handler.getApplicationHandler().getServiceLocator().getService(LambdaWarmUp.class).warmUp();
                EventLogger.logEvent(new PingEvent().setColdStart(ColdStartMarker.isSet()));
                return null;
            }

            logger.info("MotrWebHandler - handleRequest przed proxowaniem requestu");

            return handler.proxy(request, context);

        } finally {
            logger.info("MotrWebHandler - handleRequest po proxowaniu requestu");

            ColdStartMarker.unmark();
        }
    }
}
