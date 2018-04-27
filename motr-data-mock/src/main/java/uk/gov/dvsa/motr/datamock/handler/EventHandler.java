package uk.gov.dvsa.motr.datamock.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;

import uk.gov.dvsa.motr.datamock.system.MotrDataMockApplication;

public class EventHandler {

    private final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    public EventHandler() {

        MotrDataMockApplication application = new MotrDataMockApplication();
        handler = JerseyLambdaContainerHandler.getAwsProxyHandler(application);
    }

    public AwsProxyResponse handle(AwsProxyRequest request, Context context) {

        return handler.proxy(request, context);
    }
}
