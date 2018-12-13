package uk.gov.dvsa.motr.web.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;

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

    private static JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    /**
     * Executes once per container instance
     */
    public MotrWebHandler() {

        MotrWebApplication application = new MotrWebApplication();
        configureApplication(application);
        handler = JerseyLambdaContainerHandler.getAwsProxyHandler(application);
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

            if (request.isPing()) {
                handler.getApplicationHandler().getServiceLocator().getService(LambdaWarmUp.class).warmUp();
                EventLogger.logEvent(new PingEvent().setColdStart(ColdStartMarker.isSet()));
                return null;
            }

            return handler.proxy(request, context);

        } finally {
            ColdStartMarker.unmark();
        }
    }

    protected void configureApplication(MotrWebApplication application) {

    }
}
