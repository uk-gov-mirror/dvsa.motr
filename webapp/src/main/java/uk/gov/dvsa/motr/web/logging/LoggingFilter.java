package uk.gov.dvsa.motr.web.logging;

import com.amazonaws.serverless.proxy.internal.model.ApiGatewayRequestContext;
import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.MDC;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.AccessEvent;
import uk.gov.dvsa.motr.web.performance.ColdStartMarker;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;


/**
 * Logs request/response information
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {


    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        setExecutionContext(requestContext);

        String queryString = requestContext.getUriInfo().getRequestUri().getRawQuery();
        int responseLength = (responseContext.getEntity() instanceof String) ? ((String) responseContext.getEntity()).length() : -1;
        String sourceIp = getLambdaRequest(requestContext).getIdentity().getSourceIp();
        
        AccessEvent accessEvent = new AccessEvent()
                .setRequestMethod(requestContext.getMethod())
                .setRequestPath(requestContext.getUriInfo().getPath())
                .setQueryString(queryString != null ? queryString : "")
                .setStatusCode(responseContext.getStatus())
                .setResponseBodyLength(responseLength)
                .setColdStart(ColdStartMarker.isSet())
                .setSourceIp(sourceIp != null ? sourceIp : "");

        EventLogger.logEvent(accessEvent);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        setExecutionContext(requestContext);
    }

    private void setExecutionContext(ContainerRequestContext requestContext) {

        Context ctx =  (Context) requestContext.getProperty("com.amazonaws.lambda.context");
        MDC.put("lambdaFunction", ctx.getFunctionName() + ":" + ctx.getFunctionVersion());
    }

    private ApiGatewayRequestContext getLambdaRequest(ContainerRequestContext requestContext) {

        return (ApiGatewayRequestContext) requestContext.getProperty("com.amazonaws.apigateway.request.context");
    }
}
