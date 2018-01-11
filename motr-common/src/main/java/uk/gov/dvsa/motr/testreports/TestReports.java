package uk.gov.dvsa.motr.testreports;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;

import java.lang.reflect.Method;
import java.util.Set;

public class TestReports {

    public static void createTestReport(String moduleName, String packageName, Class testAnnotationClass) {

        Reflections reflections =
                new Reflections(packageName, new MethodAnnotationsScanner());
        Set<Method> resources =
                reflections.getMethodsAnnotatedWith(testAnnotationClass);
        
        new TestReportsOutput(resources, moduleName);
    }

}
