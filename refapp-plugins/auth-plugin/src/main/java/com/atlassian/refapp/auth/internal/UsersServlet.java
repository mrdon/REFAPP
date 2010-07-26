package com.atlassian.refapp.auth.internal;

import com.atlassian.user.UserManager;
import com.atlassian.user.User;
import com.atlassian.user.EntityException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;

public class UsersServlet extends HttpServlet
{
    private final UserManager userManager;
    private final VelocityEngine velocity;

    public UsersServlet(UserManager userManager)
    {
        this.userManager = userManager;
        velocity = new VelocityEngine();
        velocity.addProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, JdkLogChute.class.getName());
        velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    }

    private void sendRedirect(HttpServletResponse response, HttpServletRequest request, String path) throws IOException
    {
        response.sendRedirect(request.getRequestURL().append(path).toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
            response.setContentType("text/html");
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1)
            {
                final List<User> users = new ArrayList<User>();
                Iterator iter = userManager.getUsers().iterator();
                while (iter.hasNext())
                {
                    users.add((User) iter.next());
                }
                render("/users.vm", response, "users", users);
            }
            else
            {
                String username = pathInfo.substring(1);
                User user = userManager.getUser(username);
                render("/profile.vm", response, "user", user);
            }
        }
        catch (EntityException e)
        {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
            User user;
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1)
            {
                String name = request.getParameter("name");
                user = userManager.createUser(name);
                updateUser(user, request);
                sendRedirect(response, request, "/" + name);
            }
            else
            {
                // Supporting PUT with POST because HTML doesn't support PUT
                String username = pathInfo.substring(1);
                user = userManager.getUser(username);
                updateUser(user, request);
                sendRedirect(response, request, "");
            }
        }
        catch (EntityException e)
        {
            throw new ServletException(e);
        }
    }

    private void updateUser(User user, HttpServletRequest request) throws EntityException
    {
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        user.setEmail(email);
        user.setFullName(fullName);
        userManager.saveUser(user);
        userManager.alterPassword(user, password);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
            String pathInfo = request.getPathInfo();
            String username = pathInfo.substring(1);
            User user = userManager.getUser(username);
            updateUser(user, request);
            sendRedirect(response, request, "");
        }
        catch (EntityException e)
        {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
            String pathInfo = request.getPathInfo();
            String username = pathInfo.substring(1);
            User user = userManager.getUser(username);
            userManager.removeUser(user);
            sendRedirect(response, request, "");
        }
        catch (EntityException e)
        {
            throw new ServletException(e);
        }
    }

    private void render(String templateName, HttpServletResponse response, Object... context)
        throws ServletException
    {
        VelocityContext velocityContext = new VelocityContext();
        for (int i = 0; i < context.length - 1; i += 2)
        {
            velocityContext.put((String) context[i], context[i + 1]);
        }
        try
        {
            Template template = velocity.getTemplate(templateName);
            template.merge(velocityContext, response.getWriter());
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }

    }
}
