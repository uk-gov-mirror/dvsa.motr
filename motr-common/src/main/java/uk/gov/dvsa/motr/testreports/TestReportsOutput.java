package uk.gov.dvsa.motr.testreports;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.file.Files.newBufferedWriter;

public class TestReportsOutput {

    private static final String CSS_STYLES = String.join("\n",
            "body {font-family:monospace;color:#444}",
            "table {border-collapse:collapse;border:1px solid #ddd}",
            "td, th {padding: 10px;border:1px solid #aaa}",
            "td:first-child {word-break:break-all}",
            "tr:nth-child(even) {background-color:#ededed;}",
            "th {background-color:#f90;color:#fff;border:1px solid #f90;font-size:1.2em}",
            "thead th {background-color:#444; border:1px solid #444}"
    );
    private static final String HTML_TEMPLATE =
            "<html><head><style>" + CSS_STYLES + "</style></head>\n<body>\n%s\n</body>\n</html>";
    private static final String HTML_TABLE_TEMPLATE =
            "<table><thead><tr><th>Test Name</th><th>Test Description</th></tr></thead><tbody>%s</tbody></table>";
    private static final String HEADING_TEMPLATE = "<h1>%s</h1>";
    private static final String NO_TESTS_TEMPLATE = "<h2>This module has no tests.</h2>";
    private static final String NEW_CLASS_ROW_TEMPLATE = "<tr><th colspan=\"2\">%s</th></tr>";
    private static final String METHOD_ROW_TEMPLATE = "<tr><td>%s</td><td>%s</td></tr>";


    private static final String OUTPUT_NAME = "report.html";

    public TestReportsOutput(Set<Method> methodsSet, String moduleName) {

        List<Method> methods = new ArrayList(methodsSet);

        methods.sort(
                (Method m1, Method m2) -> m1.getDeclaringClass().toString().compareTo(m2.getDeclaringClass().toString()));

        String output = String.format(HEADING_TEMPLATE, moduleName);
        output += methods.isEmpty() ? NO_TESTS_TEMPLATE : createMethodsTable(methods, moduleName);
        output = String.format(HTML_TEMPLATE, output);

        saveReport(output);
    }

    private static void saveReport(String output) {

        try {
            Path path = TestReportsOutput.createReportPath(OUTPUT_NAME);
            try {
                BufferedWriter out = newBufferedWriter(path);
                out.write(output);
                out.close();
            } catch (NoSuchFileException e) {
                System.out.println("Could not write to " + OUTPUT_NAME);
            }
        } catch (IOException exception) {
            System.out.println("Could not create " + OUTPUT_NAME);
        }
    }

    private static String createMethodsTable(List<Method> methods, String moduleName) {

        String output = "";
        String shortClass = "";

        for (Method method : methods) {

            String fullClass = method.getDeclaringClass().toString();
            String newShortClass = fullClass.substring(fullClass.lastIndexOf(".") + 1);

            if (!newShortClass.equals(shortClass)) {
                output += String.format(NEW_CLASS_ROW_TEMPLATE, newShortClass);
                shortClass = newShortClass;
            }

            output += String.format(METHOD_ROW_TEMPLATE, method.getName(), convertCamelCaseToSentence(method.getName()));
        }

        return String.format(HTML_TABLE_TEMPLATE, output);
    }

    private   static Path createReportPath(String fileName) throws IOException {

        Path directoryPath = Paths.get("");
        Path filePath = directoryPath.resolve(fileName);
        if (!Files.exists(directoryPath)) {
            Files.createDirectory(directoryPath);
            Files.createFile(filePath);
        }
        return filePath;
    }

    private static String convertCamelCaseToSentence(String name) {

        if (!name.equals("")) {
            name =  StringUtils.capitalize(
                    StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(name)), ' ')
                    .replace(" _ ", " ").toLowerCase()
            );
        }
        return name;
    }
}
