package com.atlassian.refapp.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

public final class DefaultWebSudoAuthenticator implements WebSudoAuthenticator
{

    private final static long DEFAULT_EXPIRY_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final String WEBSUDO_SESSION_KEY = DefaultWebSudoAuthenticator.class.getName() + "-session";

    public boolean isWebSudoSession(HttpServletRequest request)
    {
        final HttpSession session = request.getSession(false);
        if (null == session)
        {
            return false;
        }
        final Long timestamp = (Long) session.getAttribute(WEBSUDO_SESSION_KEY);
        return null != timestamp && timestamp >= System.currentTimeMillis() - DEFAULT_EXPIRY_MILLIS;
    }

    public void createWebSudoSession(HttpServletRequest request)
    {
        final HttpSession session = request.getSession(true);
        if (null == session)
        {
            throw new SecurityException("Unable to create a WebSudo session.");
        }
        session.setAttribute(WEBSUDO_SESSION_KEY, System.currentTimeMillis());
    }

    public void removeWebSudoSession(HttpServletRequest request)
    {
        final HttpSession session = request.getSession(false);
        if (null == session)
        {
            return;
        }
        session.removeAttribute(WEBSUDO_SESSION_KEY);
    }
}
