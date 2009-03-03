package com.atlassian.refapp.auth.internal;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Handles log out for the reference app.  Used instead of the Seraph {@link com.atlassian.seraph.logout.LogoutServlet}
 * because that requires a hardcoded redirect URL.
 */
public class LogoutServlet extends HttpServlet
{
    private final Authenticator auth;

    public LogoutServlet(Authenticator auth)
    {
        this.auth = auth;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            auth.logout(request, response);
            RedirectHelper.redirect(request, response);
        }
        catch (AuthenticatorException e)
        {
            throw new ServletException(e);
        }
    }
}
