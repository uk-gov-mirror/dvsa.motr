package uk.gov.dvsa.motr.web.performance.warmup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.service.notify.NotificationClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Responsible for warming up all entities that require significant initialization time during first execution.
 * The process is parallelized to optimise execution time.
 */
public class DefaultLambdaWarmUp implements LambdaWarmUp {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLambdaWarmUp.class);

    private TemplateEngine templateEngine;

    private VehicleDetailsClient vehicleDetailsClient;

    private NotificationClient notificationClient;

    private int warmUpTimeoutSeconds;

    public DefaultLambdaWarmUp(
            TemplateEngine templateEngine,
            VehicleDetailsClient vehicleDetailsClient,
            NotificationClient notificationClient,
            int warmUpTimeoutSeconds) {

        this.templateEngine = templateEngine;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.notificationClient = notificationClient;
        this.warmUpTimeoutSeconds = warmUpTimeoutSeconds;
    }

    public void warmUp() {

        ExecutorService warmUpExecutor = Executors.newCachedThreadPool();

        FutureTask<Void> templatesWarmUp = new FutureTask<>(() -> {

            templateEngine.precompile("master");
            templateEngine.precompile("home");
            templateEngine.precompile("vrm");
            templateEngine.precompile("email");
            templateEngine.precompile("review");
            templateEngine.precompile("subscription-confirmation");

            return null;
        });

        FutureTask<Void> vehicleDetailsClientWarmUp = new FutureTask<>(() -> {

            vehicleDetailsClient.fetch("__WARM_UP_VRM__", "__WARM_UP_KEY__");
            return null;
        });

        FutureTask<Void> notifyClientWarmUp = new FutureTask<>(() -> {
            notificationClient.getNotificationById("");
            return null;
        });

        warmUpExecutor.submit(templatesWarmUp);
        warmUpExecutor.submit(vehicleDetailsClientWarmUp);
        warmUpExecutor.submit(notifyClientWarmUp);

        warmUpExecutor.shutdown();

        try {
            warmUpExecutor.awaitTermination(warmUpTimeoutSeconds, SECONDS);
        } catch (Exception interruptedException) {
            logger.error("Executor interrupted exception", interruptedException);
        }
    }
}
