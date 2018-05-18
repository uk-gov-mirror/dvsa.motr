package uk.gov.dvsa.motr.notify;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotifyTemplateEngine {

    private static final String BASE_PATH = "/template/notify/";
    private static Map<String, String> cachedTemplates = new HashMap<>();

    private final NotifyTemplateEngineFileReader notifyTemplateEngineFileReader;

    public NotifyTemplateEngine() {
        this.notifyTemplateEngineFileReader = new NotifyTemplateEngineFileReader();
    }

    public NotifyTemplateEngine(NotifyTemplateEngineFileReader notifyTemplateEngineFileReader) {
        this.notifyTemplateEngineFileReader = notifyTemplateEngineFileReader;
    }

    /**
     * Generate NotifyAPI parameters, given template filenames and parameters (Email)
     * @param bodyTemplate Body template filename
     * @param subjectTemplate Subject template filename
     * @param params Template parameters
     * @return Map<String, String> Parameters for NotifyAPI
     */
    public Map<String, String> getNotifyParameters(String subjectTemplate, String bodyTemplate, Map<String, String> params)
            throws NotifyTemplateEngineException {

        String body = renderFileTemplate(bodyTemplate, params);
        if (subjectTemplate != null) {
            String subject = renderFileTemplate(subjectTemplate, params);
            return getNotifyEmailParameters(subject, body);
        }
        return getNotifySmsParameters(body);
    }

    /**
     * Generate NotifyAPI parameters, given template filenames and parameters (SMS)
     * @param bodyTemplate Body template filename
     * @param params Template parameters
     * @return Map<String, String> Parameters for NotifyAPI
     */
    public Map<String, String> getNotifyParameters(String bodyTemplate, Map<String, String> params)
            throws NotifyTemplateEngineException {
        return getNotifyParameters(null, bodyTemplate, params);
    }

    /**
     * Clear the template cache (in-memory)
     */
    public void clearTemplateCache() {
        cachedTemplates.clear();
    }

    /**
     * Gets contents of template file.
     * @param fileName Template filename
     * @param parameters Template parameters
     * @return String
     */
    private String renderFileTemplate(String fileName, Map<String, String> parameters)
            throws NotifyTemplateEngineException {

        return safeReplacer(getFileTemplate(fileName), parameters);
    }

    /**
     * Replace template params
     * @param templateString Template string
     * @param params Template parameters
     * @return String
     */
    private String replacer(String templateString, Map<String, String> params) {

        StringBuilder builder = new StringBuilder(templateString);

        for (Map.Entry<String, String> entry : params.entrySet()) {

            int start;
            String pattern = "((" + entry.getKey() + "))";
            String value = entry.getValue();

            while ((start = builder.indexOf(pattern)) != -1) {
                builder.replace(start, start + pattern.length(), value);
            }
        }
        return builder.toString();
    }

    /**
     * Replace template params, but with checks.
     * @param templateString Template string
     * @param params Template parameters
     * @return String
     * @throws NotifyTemplateEngineException If template is empty, or required parameters are missing
     */
    private String safeReplacer(String templateString, Map<String, String> params)
            throws NotifyTemplateEngineException {

        // Sanity Checking
        if (StringUtils.isBlank(templateString)) {
            throw new NotifyTemplateEngineException("Template is Empty or Whitespace", templateString, params);
        }

        // Ensure required variables are present
        List<String> allMatches = new ArrayList<>();
        Pattern regexPattern = Pattern.compile("\\(\\(([a-z0-9\\-]*)\\)\\)");
        Matcher regexMatcher = regexPattern.matcher(templateString);
        while (regexMatcher.find()) {
            allMatches.add(regexMatcher.group(1));
        }

        for (String templateVariable : allMatches) {
            if (!params.containsKey(templateVariable)) {
                throw new NotifyTemplateEngineException("Parameter '" + templateVariable + "' is not defined", templateString, params);
            }
        }

        return this.replacer(templateString, params);
    }

    /**
     * Generate NotifyAPI parameters (Email)
     * @param subject Template subject
     * @param body Template body
     * @return Map<String, String> Parameters for NotifyAPI
     */
    private Map<String, String> getNotifyEmailParameters(String subject, String body) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("subject", subject);
        parameters.put("body", body);
        return parameters;
    }

    /**
     * Generate NotifyAPI parameters (SMS)
     * @param body Template body
     * @return Map<String, String> Parameters for NotifyAPI
     */
    private Map<String, String> getNotifySmsParameters(String body) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("body", body);
        return parameters;
    }

    /**
     * Gets contents of template file.
     * @param fileName URL of file
     * @return String
     */
    private String getFileTemplate(String fileName) throws NotifyTemplateEngineException {

        URL resourceUrl = NotifyTemplateEngine.class.getResource(BASE_PATH + fileName);

        if (resourceUrl == null) {
            throw new NotifyTemplateEngineException("Template file not found: " + fileName);
        }

        String template;

        if (cachedTemplates.containsKey(resourceUrl.toString())) {
            template = cachedTemplates.get(resourceUrl.toString());
        } else {
            template = notifyTemplateEngineFileReader.getTemplateFileContents(resourceUrl);
            cachedTemplates.put(resourceUrl.toString(), template);
        }

        return template;
    }
}
