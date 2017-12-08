package uk.gov.dvsa.motr.smsreceiver.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.http.HttpStatus;

import uk.gov.dvsa.motr.smsreceiver.module.ConfigModule;
import uk.gov.dvsa.motr.smsreceiver.service.SmsMessageProcessor;

public class EventHandler {

    public AwsProxyResponse handle(AwsProxyRequest request, Context context) throws Exception {

        Injector injector = Guice.createInjector(new ConfigModule());
        injector.getInstance(SmsMessageProcessor.class).run(request);
        return new AwsProxyResponse(HttpStatus.SC_OK);
    }
}
