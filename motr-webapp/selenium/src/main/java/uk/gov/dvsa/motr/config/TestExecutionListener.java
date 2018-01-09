package uk.gov.dvsa.motr.config;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import uk.gov.dvsa.motr.logging.Logger;

public class TestExecutionListener extends TestListenerAdapter implements ITestListener {

    // Keep these the same length for easier-to-read output
    private static final String RUNNING = "Running:             ";
    private static final String SKIPPED = "SKIPPED (%8.1f)s: ";
    private static final String FAILURE = "FAILURE (%8.1f)s: ";
    private static final String SUCCESS = "Success (%8.1f)s: ";

    @Override
    public void onTestStart(ITestResult result) {

        super.onTestStart(result);
        Logger.info(RUNNING + printable(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        super.onTestSuccess(result);
        Logger.info(String.format(SUCCESS, duration(result)) + printable(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {

        super.onTestFailure(result);
        Logger.info(String.format(FAILURE, duration(result)) + printable(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {

        super.onTestSkipped(result);
        Logger.info(String.format(SKIPPED, duration(result)) + printable(result));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

        super.onTestFailedButWithinSuccessPercentage(result);
        Logger.info("Failure within success %: " + printable(result));
    }

    private String printable(ITestResult result) {

        return result.getTestClass().getName() + "." + result.getName();
    }

    private float duration(ITestResult result) {

        return (result.getEndMillis() - result.getStartMillis()) / 1000f;
    }

}
