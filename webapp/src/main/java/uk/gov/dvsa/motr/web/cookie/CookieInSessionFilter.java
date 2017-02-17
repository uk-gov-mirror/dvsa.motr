package uk.gov.dvsa.motr.web.cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.session.SessionMalformedEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;

@Provider
public class CookieInSessionFilter implements ContainerResponseFilter, ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CookieInSessionFilter.class);
    private MotrSession motrSession;

    @Inject
    public CookieInSessionFilter(MotrSession motrSession) {

        this.motrSession = motrSession;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        try {
            getSessionFromCookie(requestContext);
        } catch (Exception e) {
            EventLogger.logErrorEvent(new SessionMalformedEvent().setSessionCookieValue(getSessionCookie(requestContext).getValue()), e);
            throw new NotFoundException();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        storeSessionInCookie(requestContext, responseContext);
    }

    private void getSessionFromCookie(ContainerRequestContext requestContext) throws IOException, ClassNotFoundException {

        Cookie sessionCookie = getSessionCookie(requestContext);
        this.motrSession.clear();
        if (null != sessionCookie) {
            CookieSession cookieSession = (CookieSession) fromString(sessionCookie.getValue());
            if (cookieSession.getAttributes() != null && !cookieSession.getAttributes().isEmpty()) {
                cookieSession.getAttributes().forEach(this.motrSession::setAttribute);
            }
        }
    }

    private void storeSessionInCookie(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        CookieSession cookieSession = new CookieSession();
        this.motrSession.getAttributes().forEach(cookieSession::setAttribute);

        responseContext.getHeaders().add("Set-Cookie",
                getSecureHttpOnlyCookieHeader("session", toString(cookieSession)));
        this.motrSession.clear();
    }

    private String getSecureHttpOnlyCookieHeader(String key, Object value) {

        logger.debug("getSecureHttpOnlyCookieHeader has isShouldClearCookies value of {}",
                this.motrSession.isShouldClearCookies());
        int maxAge = this.motrSession.isShouldClearCookies() ? 0 : NewCookie.DEFAULT_MAX_AGE;

        NewCookie newCookie = new NewCookie(key, value.toString(), "/", null, null, maxAge, true, true);
        logger.debug("NewCookie has value of {}", newCookie.toString());

        return newCookie.toString();
    }

    private Cookie getSessionCookie(ContainerRequestContext requestContext) {

        Map<String, Cookie> cookies = requestContext.getCookies();
        if (cookies != null) {
            return cookies.get("session");
        }
        return null;
    }

    private String toString(Serializable object) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    private Object fromString(String string) throws IOException, ClassNotFoundException {

        byte[] data = Base64.getDecoder().decode(string);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

}
