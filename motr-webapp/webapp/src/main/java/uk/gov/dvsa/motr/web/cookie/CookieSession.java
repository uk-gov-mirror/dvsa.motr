package uk.gov.dvsa.motr.web.cookie;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CookieSession implements Serializable {

    private Map<String, Object> attributes;

    public CookieSession() {

        this.attributes = new HashMap<>();
    }

    public Map<String, Object> getAttributes() {

        return attributes;
    }

    public CookieSession setAttributes(Map<String, Object> attributes) {

        this.attributes = attributes;
        return this;
    }

    public void setAttribute(String attributeKey, Object attributeValue) {

        this.attributes.put(attributeKey, attributeValue);
    }

    @Override
    public String toString() {
        return "CookieSession{" +
                "attributes=" + attributes +
                '}';
    }
}
