package uk.gov.dvsa.motr.web.system.binder;

import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.performance.warmup.DefaultLambdaWarmUp;
import uk.gov.dvsa.motr.web.performance.warmup.LambdaWarmUp;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LambdaWarmUpFactoryTest {

    @Test
    public void useNoopWarmUpWhenWarmUpTurnedOff() {

        assertEquals(LambdaWarmUp.NOOP, provideWarmUpWithFlagSetTo(false));
    }

    @Test
    public void useDefaultLambdaWarmUpWhenWarmUpTurnedOn() {

        assertTrue(DefaultLambdaWarmUp.class.isInstance(provideWarmUpWithFlagSetTo(true)));
    }

    private static LambdaWarmUp provideWarmUpWithFlagSetTo(boolean flagState) {

        return new LambdaWarmUpBinder.LambdaWarmUpFactory(
                mock(TemplateEngine.class),
                mock(VehicleDetailsClient.class),
                flagState, 10).provide();
    }
}
