package uk.gov.dvsa.motr.notify;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyTemplateEngineTest {

    private static final String TEST_SUBJECT_FILENAME = "test-subject.txt";
    private static final String TEST_BODY_FILENAME = "test-body.txt";
    private NotifyTemplateEngineFileReader notifyTemplateEngineFileReader = mock(NotifyTemplateEngineFileReader.class);
    private NotifyTemplateEngine notifyTemplateEngine;

    @Before
    public void setUp() {
        this.notifyTemplateEngine = new NotifyTemplateEngine(notifyTemplateEngineFileReader);
    }

    @After
    public void cleanUpCache() {
        this.notifyTemplateEngine.clearTemplateCache();
    }

    @Test
    public void engineCorrectlyRendersEmailTemplateWithParameters() throws NotifyTemplateEngineException {

        when(this.notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("((var-a)) ((var-b))!").thenReturn("The subject of ((var-a))");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var-a", "Hello");
        parameters.put("var-b", "World");

        Map<String, String> personalisation =
                notifyTemplateEngine.getNotifyParameters(TEST_SUBJECT_FILENAME, TEST_BODY_FILENAME, parameters);

        verify(this.notifyTemplateEngineFileReader, times(2)).getTemplateFileContents(any());

        Assert.assertEquals("The subject of Hello", personalisation.get("subject"));
        Assert.assertEquals("Hello World!", personalisation.get("body"));
    }

    @Test
    public void engineCorrectlyRendersEmailTemplateWithoutParameters() throws NotifyTemplateEngineException {

        when(this.notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("Hello World!").thenReturn("The subject of Hello");

        Map<String, String> parameters = new HashMap<>();

        Map<String, String> personalisation =
                notifyTemplateEngine.getNotifyParameters(TEST_SUBJECT_FILENAME, TEST_BODY_FILENAME, parameters);

        verify(this.notifyTemplateEngineFileReader, times(2)).getTemplateFileContents(any());

        Assert.assertEquals("The subject of Hello", personalisation.get("subject"));
        Assert.assertEquals("Hello World!", personalisation.get("body"));
    }

    @Test
    public void engineCorrectlyRendersSmsTemplateWithParameters() throws NotifyTemplateEngineException {

        when(notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("((var-a)) ((var-b))!");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var-a", "Hello");
        parameters.put("var-b", "World");

        Map<String, String> personalisation =
                notifyTemplateEngine.getNotifyParameters(TEST_BODY_FILENAME, parameters);

        verify(notifyTemplateEngineFileReader, times(1)).getTemplateFileContents(any());

        Assert.assertFalse("The 'subject' key is defined", personalisation.containsKey("subject"));
        Assert.assertEquals("Hello World!", personalisation.get("body"));
    }

    @Test
    public void engineCorrectlyRendersSmsTemplateWithoutParameters() throws NotifyTemplateEngineException {

        when(notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("Hello World!");

        Map<String, String> parameters = new HashMap<>();

        Map<String, String> personalisation =
                notifyTemplateEngine.getNotifyParameters(TEST_BODY_FILENAME, parameters);

        verify(notifyTemplateEngineFileReader, times(1)).getTemplateFileContents(any());

        Assert.assertFalse("The 'subject' key is defined", personalisation.containsKey("subject"));
        Assert.assertEquals("Hello World!", personalisation.get("body"));
    }

    @Test (expected = NotifyTemplateEngineException.class)
    public void engineThrowsExceptionWhenMissingTemplateParameter() throws NotifyTemplateEngineException {

        when(notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("((var-a)) ((var-b))!").thenReturn("The subject of ((var-a))");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var-a", "Hello");

        notifyTemplateEngine.getNotifyParameters(TEST_SUBJECT_FILENAME, TEST_BODY_FILENAME, parameters);
    }

    @Test (expected = NotifyTemplateEngineException.class)
    public void engineThrowsExceptionWhenTemplateIsEmpty() throws NotifyTemplateEngineException {

        when(notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("");

        Map<String, String> parameters = new HashMap<>();

        notifyTemplateEngine.getNotifyParameters(TEST_SUBJECT_FILENAME, TEST_BODY_FILENAME, parameters);
    }

    @Test (expected = NotifyTemplateEngineException.class)
    public void engineThrowsExceptionWhenTemplateIsOnlyWhitespace() throws NotifyTemplateEngineException {

        when(notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("    ");

        Map<String, String> parameters = new HashMap<>();

        notifyTemplateEngine.getNotifyParameters(TEST_SUBJECT_FILENAME, TEST_BODY_FILENAME, parameters);
    }

    @Test (expected = NotifyTemplateEngineException.class)
    public void engineThrowsExceptionWhenTemplateNotFound() throws NotifyTemplateEngineException {

        Map<String, String> parameters = new HashMap<>();

        notifyTemplateEngine.getNotifyParameters("some-non-existent-file", parameters);
    }

    @Test
    public void engineCachesTemplatesThatAreSubsequentlyUsed() throws NotifyTemplateEngineException {

        when(notifyTemplateEngineFileReader.getTemplateFileContents(any()))
                .thenReturn("Hello World!");

        Map<String, String> parameters = new HashMap<>();

        // One file system call
        notifyTemplateEngine.getNotifyParameters(TEST_BODY_FILENAME, parameters);
        notifyTemplateEngine.getNotifyParameters(TEST_BODY_FILENAME, parameters);

        // One filesystem call
        notifyTemplateEngine.getNotifyParameters(TEST_SUBJECT_FILENAME, parameters);

        verify(notifyTemplateEngineFileReader, times(2)).getTemplateFileContents(any());
    }
}
