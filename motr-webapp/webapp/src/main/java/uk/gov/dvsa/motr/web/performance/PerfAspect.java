package uk.gov.dvsa.motr.web.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.eventlog.TimedEvent;

@Aspect
public class PerfAspect {

    @Pointcut("within(uk.gov.dvsa.motr..*)")
    public void withinMotrWeb() {
    }

    @Pointcut("execution(public * *(..))")
    public void anyPublicMethod() {
    }

    @Pointcut("execution(* uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient.*(..))")
    public void vehicleDetailsClientCall() {
    }

    @Pointcut("execution(* uk.gov.dvsa.motr.notifications.service.NotifyService.*(..))")
    public void notifyServiceCall() {
    }

    @Pointcut("execution(* uk.gov.dvsa.motr.web.encryption.AwsKmsDecryptor.*(..))")
    public void decryptorCall() {
    }

    @Pointcut("execution(* uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbSubscriptionRepository.*(..))")
    public void repositoryCall() {
    }

    @Pointcut("vehicleDetailsClientCall() || repositoryCall() || notifyServiceCall() || decryptorCall()")
    public void remoteCall() {
    }

    @Around("withinMotrWeb() && anyPublicMethod() && remoteCall()")
    public Object around(ProceedingJoinPoint pointcut) throws Throwable {

        Object response;
        long start = System.currentTimeMillis();
        String name = pointcut.getSignature().toShortString();

        try {

            response = pointcut.proceed();

        } finally {

            long end = System.currentTimeMillis() - start;
            EventLogger.logEvent(new TimedEvent().setName(name).setTime(end));
        }

        return response;
    }
}