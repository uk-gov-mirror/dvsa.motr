package uk.gov.dvsa.motr.web.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.glassfish.hk2.api.ServiceLocator;

import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.performance.ColdStartMarker;
import uk.gov.dvsa.motr.web.performance.warmup.LambdaWarmUp;
import uk.gov.dvsa.motr.web.system.MotrWebApplication;

import static uk.gov.dvsa.motr.web.logging.LogConfigurator.configureLogging;

/**
 * Entry point for Lambda
 */
public class MotrWebHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    private final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    /**
     * Executes once per container instance
     */
    public MotrWebHandler() {

        MotrWebApplication application = new MotrWebApplication();
        handler = JerseyLambdaContainerHandler.getAwsProxyHandler(application);
        ServiceLocator locator = handler.getApplicationHandler().getServiceLocator();
        Config config = locator.getService(Config.class);
        configureLogging(config);
        locator.getService(LambdaWarmUp.class).warmUp();
    }

    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {

        AwsProxyResponse response = handler.proxy(request, context);
        ColdStartMarker.unmark();
        return response;
    }
}
