package uk.gov.dvsa.motr.web.cookie;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class MotrSession {

    private static final String VRM_COOKIE_ID = "regNumber";
    private static final String EMAIL_COOKIE_ID = "email";
    private static final String VISITING_FROM_REVIEW_COOKIE_ID = "visitingFromReview";

    private Map<String, Object> attributes;

    private boolean shouldClearCookies;

    public MotrSession() {

        this.attributes = new HashMap<>();
    }

    public void setShouldClearCookies(boolean shouldClearCookies) {

        this.shouldClearCookies = shouldClearCookies;
    }

    public String getRegNumberFromSession() {

        Object regFromSession = getAttribute(VRM_COOKIE_ID);
        return regFromSession == null ? "" : regFromSession.toString();
    }

    public String getEmailFromSession() {

        Object emailFromSession = getAttribute(EMAIL_COOKIE_ID);
        return emailFromSession == null ? "" : emailFromSession.toString();
    }

    public boolean visitingFromReviewPage() {

        Object visitingFromReviewPage = getAttribute(VISITING_FROM_REVIEW_COOKIE_ID);
        return (visitingFromReviewPage != null && ((Boolean) visitingFromReviewPage));
    }

    public boolean isAllowedOnEmailPage() {

        return !getRegNumberFromSession().isEmpty();
    }

    public boolean isAllowedOnPage() {

        return isAllowedOnEmailPage() && !getEmailFromSession().isEmpty();
    }

    public void setVisitingFromReview(boolean visitingFromReview) {

        this.setAttribute(VISITING_FROM_REVIEW_COOKIE_ID, visitingFromReview);
    }

    public void setEmail(String emailValue) {

        this.setAttribute(EMAIL_COOKIE_ID, emailValue);
    }

    public void setVrm(String vrmValue) {

        this.setAttribute(VRM_COOKIE_ID, vrmValue);
    }

    protected void setAttribute(String attributeKey, Object attributeValue) {

        this.attributes.put(attributeKey, attributeValue);
    }

    protected Object getAttribute(String attributeKey) {

        return this.attributes.get(attributeKey);
    }

    protected boolean isShouldClearCookies() {

        return shouldClearCookies;
    }

    protected Map<String, Object> getAttributes() {

        return attributes;
    }

    protected void clear() {

        this.attributes.clear();
        shouldClearCookies = false;
    }

    @Override
    public String toString() {
        return "MotrSession{" +
                "attributes=" + attributes +
                '}';
    }
}
