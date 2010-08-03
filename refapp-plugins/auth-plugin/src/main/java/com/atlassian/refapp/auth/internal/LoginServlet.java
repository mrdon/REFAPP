package com.atlassian.refapp.auth.internal;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.EscapeTool;

import com.atlassian.seraph.auth.Authenticator;

public class    LoginServlet extends BaseVelocityServlet
{
    private final Authenticator auth;

    public LoginServlet(Authenticator auth)
    {
        super();
        this.auth = auth;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");

        VelocityContext context = createDefaultVelocityContext();
        context.put("redir", getContextRelativeRequestURL(request));

        Principal user = auth.getUser(request);
        if (user == null)
        {
            context.put("loginURI", request.getContextPath() + "/plugins/servlet/login");
            getTemplate("/login.vm").merge(context, response.getWriter());
        }
        else
        {
            context.put("logoutURI", request.getContextPath() + "/plugins/servlet/logout");
            context.put("user", user);
            getTemplate("/user.vm").merge(context, response.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        RedirectHelper.redirect(req, resp);
    }

    private String getContextRelativeRequestURL(HttpServletRequest request)
    {
        if (request.getParameter("redir") != null)
        {
            return request.getParameter("redir");
        }
        
        StringBuilder redir = new StringBuilder(request.getServletPath());
        String path = request.getPathInfo();
        if (path != null && path.length() > 0)
        {
            redir.append(path);
        }
        String query = request.getQueryString();
        if (query != null && query.length() > 0)
        {
            redir.append('?').append(query);
        }
        return redir.toString();
    }
}
