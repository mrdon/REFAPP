package com.atlassian.refapp.auth.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.refapp.auth.external.WebSudoSessionManager;

/**
 * Drops the WebSudo session.
 */
public class DropWebSudoServlet extends HttpServlet
{
    private final WebSudoSessionManager sessionManager;

    public DropWebSudoServlet(WebSudoSessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        sessionManager.removeWebSudoSession(request);
        RedirectHelper.redirect(request, response);
    }
}
