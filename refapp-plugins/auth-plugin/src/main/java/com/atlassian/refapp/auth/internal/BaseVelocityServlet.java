package com.atlassian.refapp.auth.internal;


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

abstract class BaseVelocityServlet extends HttpServlet
{
    // JavaDocs say this is threadsafe & stateless, so might as well share an instance
    // http://velocity.apache.org/tools/releases/1.3/javadoc/org/apache/velocity/tools/generic/EscapeTool.html
    private static final EscapeTool ESCAPE_TOOL = new EscapeTool();

    private final VelocityEngine velocity;

    public BaseVelocityServlet()
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

    VelocityContext createDefaultVelocityContext()
    {
        final VelocityContext context = new VelocityContext();
        context.put("esc", ESCAPE_TOOL);
        return context;
    }

    Template getTemplate(final String templateName) throws ServletException
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
