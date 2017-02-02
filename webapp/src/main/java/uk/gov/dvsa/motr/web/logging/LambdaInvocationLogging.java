package uk.gov.dvsa.motr.web.logging;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.MDC;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.AccessEvent;

import java.util.function.BiFunction;


/**
 * Logs request/response information about handling proxy event
 */
public class LambdaInvocationLogging {

    public static AwsProxyResponse invokeWithLogging(
            AwsProxyRequest request,
            Context ctx,
            BiFunction<AwsProxyRequest, Context, AwsProxyResponse> handler
    ) {
        MDC.put("lambdaFunction", ctx.getFunctionName() + ":" + ctx.getFunctionVersion());

        AccessEvent accessEvent = new AccessEvent()
                .setRequestMethod(request.getHttpMethod())
                .setRequestPath(request.getPath())
                .setRequestBodyLength(request.getBody() != null ? request.getBody().length() : 0)
                .setQueryString(request.getQueryString());
        try {

            AwsProxyResponse response = handler.apply(request, ctx);
            accessEvent
                    .statusCode(response.getStatusCode())
                    .setResponseBodyLength(response.getBody() != null ? response.getBody().length() : 0);

            EventLogger.logEvent(accessEvent);

            return response;

        } catch (Exception e) {

            EventLogger.logErrorEvent(accessEvent, e);
            throw new RuntimeException(e);
        }
    }
}
