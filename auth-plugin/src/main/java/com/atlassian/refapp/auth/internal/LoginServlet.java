package com.atlassian.refapp.auth.internal;

import java.io.IOException;
import java.io.Writer;

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

public class LoginServlet extends HttpServlet
{
    private final VelocityEngine velocity;
    
    public LoginServlet()
    {
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
        Template template = getTemplate("/login.vm");
        render(template, response.getWriter());
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
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
    
    private void render(Template template, Writer writer) throws IOException
    {
        VelocityContext context = new VelocityContext();
        template.merge(context, writer);
    }
}
