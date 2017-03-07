package uk.gov.dvsa.motr.web.system.binder;

import org.junit.Test;

import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.performance.warmup.DefaultLambdaWarmUp;
import uk.gov.dvsa.motr.web.performance.warmup.LambdaWarmUp;

import javax.inject.Provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DO_WARM_UP;
import static uk.gov.dvsa.motr.web.system.SystemVariable.WARM_UP_TIMEOUT_SEC;

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

        Config config = mock(Config.class);
        when(config.getValue(eq(DO_WARM_UP))).thenReturn(Boolean.valueOf(flagState).toString());
        when(config.getValue(eq(WARM_UP_TIMEOUT_SEC))).thenReturn(Integer.valueOf(10).toString());

        return new LambdaWarmUpBinder.LambdaWarmUpFactory(
                mock(Provider.class),
                mock(Provider.class),
                mock(Provider.class),
                config).provide();
    }
}
