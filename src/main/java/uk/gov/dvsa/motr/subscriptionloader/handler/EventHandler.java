package uk.gov.dvsa.motr.subscriptionloader.handler;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.subscriptionloader.module.ConfigModule;
import uk.gov.dvsa.motr.subscriptionloader.module.InvocationContextModule;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.Loader;


public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public EventHandler() {

    }

    public void handle(AwsCloudwatchEvent request, Context context) throws Exception {

        logger.info("Request: {}, context: {}", request, context);
        Injector injector = Guice.createInjector(
                new InvocationContextModule(context),
                new ConfigModule()
        );

        injector.getInstance(Loader.class).run(request.getTimeAsDateTime().toLocalDate());
    }


    public static void main(String[] args) throws Exception {

        new EventHandler().handle(new AwsCloudwatchEvent().setTime("2011-01-01T12:12:12Z"), new Context() {
            @Override
            public String getAwsRequestId() {
                return "AAgregregregre";
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public String getInvokedFunctionArn() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        });
    }
}
