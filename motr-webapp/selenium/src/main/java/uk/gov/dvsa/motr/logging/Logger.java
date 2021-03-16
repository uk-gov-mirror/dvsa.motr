package uk.gov.dvsa.motr.logging;

import org.testng.Reporter;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    public static void info(String logText) {

        String logInfo = ("INFO: [" + logText + "]");
        Reporter.log(logInfo, true);
    }

    public static void error(String logText) {

        String logErr = ("ERROR: [" + logText + "]");
        Reporter.log(logErr, true);
    }

    public static void error(String logText, Exception ex) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);

        String logErr = ("ERROR: [" + logText + "]\n" + stringWriter.toString());

        Reporter.log(logErr, true);
    }
}

