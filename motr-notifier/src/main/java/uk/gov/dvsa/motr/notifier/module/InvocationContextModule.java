package uk.gov.dvsa.motr.notifier.module;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.AbstractModule;

public class InvocationContextModule extends AbstractModule {

    private Context context;

    public InvocationContextModule(Context context) {

        this.context = context;
    }

    @Override
    protected void configure() {

        bind(Context.class).toInstance(context);
    }
}
