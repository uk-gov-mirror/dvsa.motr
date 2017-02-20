package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.performance.warmup.DefaultLambdaWarmUp;
import uk.gov.dvsa.motr.web.performance.warmup.LambdaWarmUp;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;
import uk.gov.service.notify.NotificationClient;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DO_WARM_UP;
import static uk.gov.dvsa.motr.web.system.SystemVariable.WARM_UP_TIMEOUT_SEC;

public class LambdaWarmUpBinder extends AbstractBinder {
    @Override
    protected void configure() {

        bindFactory(LambdaWarmUpFactory.class).to(LambdaWarmUp.class);
    }

    static class LambdaWarmUpFactory implements BaseFactory<LambdaWarmUp> {

        private TemplateEngine templateEngine;
        private VehicleDetailsClient vehicleDetailsClient;
        private boolean doWarmUp;
        private int warmUpTimeoutSeconds;

        @Inject
        public LambdaWarmUpFactory(
                TemplateEngine templateEngine,
                VehicleDetailsClient vehicleDetailsClient,
                @SystemVariableParam(DO_WARM_UP) Boolean warmUpFlag,
                @SystemVariableParam(WARM_UP_TIMEOUT_SEC) Integer warmUpTimeoutSeconds) {

            this.templateEngine = templateEngine;
            this.vehicleDetailsClient = vehicleDetailsClient;
            this.doWarmUp = warmUpFlag;
            this.warmUpTimeoutSeconds = warmUpTimeoutSeconds;
        }

        @Override
        public LambdaWarmUp provide() {

            if (doWarmUp) {
                return new DefaultLambdaWarmUp(
                        templateEngine,
                        vehicleDetailsClient,
                        new NotificationClient("WARM_KEY"),
                        warmUpTimeoutSeconds
                );
            } else {
                return LambdaWarmUp.NOOP;
            }
        }
    }
}


