package com.atlassian.refapp.auth;

import javax.servlet.http.HttpServletRequest;

public interface WebSudoAuthenticator
{
    boolean isWebSudoSession(HttpServletRequest request);

    void createWebSudoSession(HttpServletRequest request);

    void removeWebSudoSession(HttpServletRequest request);
}
