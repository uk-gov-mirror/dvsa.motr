package uk.gov.dvsa.motr.web.cookie;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.session.SessionMalformedEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Date.from;

import static javax.ws.rs.core.Cookie.DEFAULT_VERSION;

@Provider
public class CookieInSessionFilter implements ContainerResponseFilter, ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CookieInSessionFilter.class);
    private static final int MAX_COOKIE_AGE_IN_SECONDS = 60 * 20;
    private final MotrSession motrSession;
    private final CookieCipher cookieCipher;
    private Clock clockReference = Clock.system(ZoneId.of("Europe/London"));

    @Inject
    public CookieInSessionFilter(MotrSession motrSession, CookieCipher cookieCipher) {

        this.motrSession = motrSession;
        this.cookieCipher = cookieCipher;
    }

    public void setClock(Clock clock) {
        this.clockReference = clock;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        try {
            getSessionFromCookie(requestContext);
        } catch (Exception e) {
            EventLogger.logErrorEvent(new SessionMalformedEvent(), e);

            try {
                redirectToCookieErrorPageWithValidCookie(requestContext);
            } catch (URISyntaxException | IOException exception) {
                logger.debug("Could not redirect to /cookie-error on corrupted cookie: " + exception.toString());
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        storeSessionInCookie(responseContext);
    }

    /**
     * Creates a temporary, uncorrupted cookie to replace the corrupt one,
     * then redirects to start page which will recreate the session cookie.
     * @param requestContext
     * @throws IOException
     * @throws URISyntaxException
     */
    private void redirectToCookieErrorPageWithValidCookie(ContainerRequestContext requestContext) throws IOException, URISyntaxException {

        this.motrSession.clear();
        this.motrSession.setAttribute("dummy", "value");
        this.motrSession.setShouldClearCookies(true);

        CookieSession cookieSession = new CookieSession();
        this.motrSession.getAttributes().forEach(cookieSession::setAttribute);
        byte[] encryptedCookieSession = cookieCipher.encryptCookie(cookieSession);
        Response response = Response.status(Response.Status.FOUND).location(new URI("cookie-error")).build();
        response.getHeaders().add("Set-Cookie", getSecureHttpOnlyCookieHeader("session", encryptedCookieSession));
        requestContext.abortWith(response);
    }

    private void getSessionFromCookie(ContainerRequestContext requestContext) throws IOException, ClassNotFoundException {

        Cookie sessionCookie = getSessionCookie(requestContext);
        this.motrSession.clear();
        if (sessionCookie != null && !StringUtils.isBlank(sessionCookie.getValue())) {
            CookieSession cookieSession = cookieCipher.decryptCookie(fromString(sessionCookie.getValue()));
            if (cookieSession.getAttributes() != null && !cookieSession.getAttributes().isEmpty()) {
                cookieSession.getAttributes().forEach(this.motrSession::setAttribute);
            }
        }
    }

    private void storeSessionInCookie(ContainerResponseContext responseContext) throws IOException {

        if (!this.motrSession.getAttributes().isEmpty()) {
            CookieSession cookieSession = new CookieSession();
            this.motrSession.getAttributes().forEach(cookieSession::setAttribute);
            byte[] encryptedCookieSession = cookieCipher.encryptCookie(cookieSession);
            responseContext.getHeaders().add("Set-Cookie",
                    getSecureHttpOnlyCookieHeader("session", toString(encryptedCookieSession)));
            this.motrSession.clear();
        }
    }

    private String getSecureHttpOnlyCookieHeader(String key, Object value) {

        logger.debug("getSecureHttpOnlyCookieHeader has isShouldClearCookies value of {}",
                this.motrSession.isShouldClearCookies());
        int maxAge = this.motrSession.isShouldClearCookies() ? 0 : MAX_COOKIE_AGE_IN_SECONDS;

        Date expires = from(ZonedDateTime.now(clockReference).plus(maxAge, SECONDS)
                .toInstant());

        NewCookie newCookie = new NewCookie(
                key,
                value.toString(),
                "/",
                null,
                DEFAULT_VERSION,
                null,
                maxAge,
                expires,
                true,
                true
        );

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

    private String toString(byte[] object) {

        return Base64.getEncoder().encodeToString(object);
    }

    private byte[] fromString(String string) {

        return Base64.getDecoder().decode(string);
    }

}
