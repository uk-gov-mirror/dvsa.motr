package uk.gov.dvsa.motr.web.render;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class HandlebarsTemplateEngineTest {

    @DataProvider
    public static Object[][] rootPaths() {
        // @formatter:off
        return new Object[][]{
                {"http://assets/"},
                {"http://assets//"},
                {"http://assets"}
        };
    }
    // @formatter:on

    @UseDataProvider("rootPaths")
    @Test
    public void testEngineCorrectlyFillsTemplate(String rootPath) {

        HandlebarsTemplateEngine instance =
                new HandlebarsTemplateEngine(rootPath, "12345");

        Map<String, String> context = new HashMap<String, String>();
        context.put("key", "testValue");

        String actualOutput = instance.render("test", context);
        String expectedOutput = "http://assets/somepath/my.css?v=12345\n" +
                "testValue";

        assertEquals("Received output is wrong", expectedOutput, actualOutput);
    }
}
