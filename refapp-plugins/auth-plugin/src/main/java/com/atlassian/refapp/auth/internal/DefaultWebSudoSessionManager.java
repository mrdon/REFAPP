package com.atlassian.refapp.auth.internal;

import com.atlassian.refapp.auth.external.WebSudoSessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

public class DefaultWebSudoSessionManager implements WebSudoSessionManager
{
    private final static long DEFAULT_EXPIRY_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final String WEBSUDO_SESSION_KEY = DefaultWebSudoSessionManager.class.getName() + "-session";

    public boolean isWebSudoSession(final HttpServletRequest request)
    {
        final HttpSession session = request.getSession(false);
        if (null == session)
        {
            return false;
        }
        final Long timestamp = (Long) session.getAttribute(WEBSUDO_SESSION_KEY);
        return null != timestamp && timestamp >= currentTimeMillis() - DEFAULT_EXPIRY_MILLIS;
    }

    public void createWebSudoSession(final HttpServletRequest request)
    {
        final HttpSession session = request.getSession(true);
        if (null == session)
        {
            throw new SecurityException("Unable to create a WebSudo session.");
        }
        session.setAttribute(WEBSUDO_SESSION_KEY, currentTimeMillis());
    }

    public void removeWebSudoSession(final HttpServletRequest request)
    {
        final HttpSession session = request.getSession(false);
        if (null == session)
        {
            return;
        }
        session.removeAttribute(WEBSUDO_SESSION_KEY);
    }

    /**
     * @return  the difference, measured in milliseconds, between
     *          the current time and midnight, January 1, 1970 UTC.
     *
     * Mainly used for testing purposes.
     */
    long currentTimeMillis()
    {
        // We could introduce an explicit Clock interface and inject a clock instance instead...
        return System.currentTimeMillis();
    }
}
