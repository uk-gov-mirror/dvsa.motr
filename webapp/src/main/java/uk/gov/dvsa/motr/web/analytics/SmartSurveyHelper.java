package uk.gov.dvsa.motr.web.analytics;

import org.apache.http.client.utils.URIBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

public class SmartSurveyHelper {
    public static final String CONTACT_TYPE = "contact-type";

    private Map<String, String> attributes;
    private String templateVariable;
    private String uri;

    public SmartSurveyHelper(String uri, String templateVariable) {

        this.templateVariable = templateVariable;
        this.uri = uri;
        attributes = new HashMap<>();
    }

    public void putAttribute(String key, String value) {

        attributes.put(key, value);
    }

    public Map<String, String> formatAttributes() {

        if (attributes.isEmpty()) {
            return Collections.emptyMap();
        }
        UriBuilder builder = UriBuilder.fromUri(uri);

        Iterator entries = attributes.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry)entries.next();
            builder.queryParam((String)entry.getKey(), (String)entry.getValue());
        }

        Map<String, String> map = new HashMap<>();
        map.put(templateVariable, builder.build().toString());
        return map;
    }

    public void clear() {

        attributes.clear();
    }
}