package com.atlassian.refapp.auth.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.refapp.auth.external.WebSudoSessionManager;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import org.apache.velocity.VelocityContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

public final class WebSudoServlet extends BaseVelocityServlet
{
    private static final String LOGIN_PATH = "/plugins/servlet/login";
    private final Authenticator auth;
    private final WebSudoSessionManager webSudoSessionManager;

    public WebSudoServlet(final Authenticator auth, final WebSudoSessionManager webSudoSessionManager)
    {
        super();
        this.auth = checkNotNull(auth, "auth cannot be null");
        this.webSudoSessionManager = checkNotNull(webSudoSessionManager, "webSudoSessionManager cannot be null");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");

        final VelocityContext context = createDefaultVelocityContext();
        context.put("redir", request.getParameter("redir"));

        final Principal user = auth.getUser(request);
        if (user != null)
        {
            context.put("username", user.getName());
            context.put("websudoURI", request.getContextPath() + "/plugins/servlet/websudo");
            getTemplate("/websudo.vm").merge(context, response.getWriter());
        }
        else
        {
            redirectToLogin(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final HttpSession session = request.getSession(false);
        if (null == session)
        {
            redirectToLogin(request, response);
            return;
        }

        try
        {
            final Principal user = auth.getUser(request);
            if (auth.login(request, response, user.getName(), request.getParameter("os_password")))
            {
                webSudoSessionManager.createWebSudoSession(request);
                RedirectHelper.redirect(request, response);
            }
            else
            {
                response.sendRedirect(request.getRequestURL().append("?redir=").append(request.getParameter("redir")).toString());
            }
        } catch (AuthenticatorException ae)
        {
            webSudoSessionManager.removeWebSudoSession(request);
            redirectToLogin(request, response);
        }
    }

    private void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        response.sendRedirect(request.getContextPath() + LOGIN_PATH);
    }

}