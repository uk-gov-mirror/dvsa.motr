package uk.gov.dvsa.motr.web.performance.warmup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.service.notify.NotificationClient;

import java.util.function.Function;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultLambdaWarmUpTest {

    private TemplateEngine templateEngine;
    private VehicleDetailsClient vehicleDetailsClient;
    private NotificationClient notificationClient;

    private Function<Integer, Answer> computation = (duration) -> (invocationOnMock) -> {
        Thread.sleep(duration);
        return null;
    };

    @Before
    public void setUp() {

        templateEngine = mock(TemplateEngine.class);
        vehicleDetailsClient = mock(VehicleDetailsClient.class);
        notificationClient = mock(NotificationClient.class);
    }

    @Test(timeout = 2000)
    public void testWarmUpFinishesWithinTimeout() throws Exception {

        whenEachWarmUpComponentTakes(4000);
        warmUpWithTimeout(1);
    }

    @Test
    public void warmUpExecutesWithoutError() throws Exception {

        whenEachWarmUpComponentTakes(1);
        warmUpWithTimeout(1);
    }

    private void whenEachWarmUpComponentTakes(int millis) throws Exception {

        Answer computation = this.computation.apply(millis);

        doAnswer(computation).when(templateEngine).precompile(anyString());
        when(vehicleDetailsClient.fetch(anyString(), anyString())).thenAnswer(computation);
        when(notificationClient.getNotificationById(anyString())).thenAnswer(computation);
    }

    private void warmUpWithTimeout(int seconds) {

        new DefaultLambdaWarmUp(
                templateEngine,
                vehicleDetailsClient,
                notificationClient,
                seconds
        ).warmUp();
    }
}
