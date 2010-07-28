package com.atlassian.refapp.auth.external;

import javax.servlet.http.HttpServletRequest;

public interface WebSudoSessionManager
{
    boolean isWebSudoSession(final HttpServletRequest request);

    void createWebSudoSession(final HttpServletRequest request);

    void removeWebSudoSession(final HttpServletRequest request);
}
