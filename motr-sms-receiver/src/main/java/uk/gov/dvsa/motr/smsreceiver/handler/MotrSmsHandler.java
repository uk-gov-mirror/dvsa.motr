package uk.gov.dvsa.motr.smsreceiver.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;

import uk.gov.dvsa.motr.smsreceiver.system.MotrSmsApplication;

/**
 * Entry point for Lambda
 */
public class MotrSmsHandler {

    private final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    /**
     * Executes once per container instance
     */
    public MotrSmsHandler() {

        MotrSmsApplication application = new MotrSmsApplication();
        handler = JerseyLambdaContainerHandler.getAwsProxyHandler(application);
    }

    /**
     * Executes upon request. Request can be HTTP request proxies through API Gateway
     *
     * @param request request envelope
     * @param context lambda invocation context
     * @return null in the case of Ping request, AwsProxyResponse for HTTP requests
     */
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {

        return handler.proxy(request, context);
    }
}
