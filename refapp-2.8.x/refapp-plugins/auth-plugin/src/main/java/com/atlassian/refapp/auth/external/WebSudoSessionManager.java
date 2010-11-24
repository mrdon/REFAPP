package com.atlassian.refapp.auth.external;

import javax.servlet.http.HttpServletRequest;

public interface WebSudoSessionManager
{
    /**
     * @param request
     * @return true if the current request is made within a valid WebSudo session, false otherwise.
     */
    boolean isWebSudoSession(final HttpServletRequest request);

    /**
     * Create a new WebSudo session.
     * @param request
     */
    void createWebSudoSession(final HttpServletRequest request);

    /**
     * Remove the current WebSudo session (if there is an existing one).
     * @param request
     */
    void removeWebSudoSession(final HttpServletRequest request);
}
