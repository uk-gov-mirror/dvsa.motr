package uk.gov.dvsa.motr.notify;

import org.apache.commons.lang3.StringUtils;

import uk.gov.dvsa.motr.eventlog.EventLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class NotifyTemplateEngineFileReader {

    public String getTemplateFileContents(URL url) {

        StringBuilder result = new StringBuilder();

        String pathName = url.getPath();
        File file = new File(pathName);

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");

            }

        } catch (IOException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_LOADING_TEMPLATE),
                        exception);
            // wrapping because nothing can be done about it.
            throw new RuntimeException(exception);
        }

        return StringUtils.trim(result.toString());
    }
}
