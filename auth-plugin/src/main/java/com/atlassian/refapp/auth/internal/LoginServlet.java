package com.atlassian.refapp.auth.internal;

import com.atlassian.seraph.auth.Authenticator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.EscapeTool;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public class LoginServlet extends HttpServlet
{
    // JavaDocs say this is threadsafe & stateless, so might as well share an instance
    // http://velocity.apache.org/tools/releases/1.3/javadoc/org/apache/velocity/tools/generic/EscapeTool.html
    private static final EscapeTool ESCAPE_TOOL = new EscapeTool();

    private final Authenticator auth;
    private final VelocityEngine velocity;

    public LoginServlet(Authenticator auth)
    {
        this.auth = auth;
        velocity = new VelocityEngine();
        velocity.addProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, JdkLogChute.class.getName());
        velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    }
    
    
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        try
        {
            velocity.init();
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");

        VelocityContext context = new VelocityContext();
        context.put("esc", ESCAPE_TOOL);
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
        StringBuilder redir = new StringBuilder(req.getContextPath());

        String redirPath = req.getParameter("redir");
        if (redirPath != null && redirPath.length() > 0)
        {
            if (!redirPath.startsWith("/"))
            {
                redir.append("/");
            }
            redir.append(redirPath);
        }

        resp.sendRedirect(redir.toString());
    }

    private String getContextRelativeRequestURL(HttpServletRequest request)
    {
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

    private Template getTemplate(String templateName) throws ServletException
    {
        try
        {
            return velocity.getTemplate(templateName);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }
}
