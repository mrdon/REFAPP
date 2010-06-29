package com.atlassian.refapp.trustedapps.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang.StringUtils.isBlank;

import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever.RetrievalException;
import com.atlassian.security.auth.trustedapps.IPAddressFormatException;

public class TrustedAppsManagementServlet extends HttpServlet
{
    private final VelocityEngine velocity;
    private final TrustedApplicationsConfigurationManager trustedAppsManager;

    public TrustedAppsManagementServlet(final TrustedApplicationsConfigurationManager trustedAppsManager)
            throws Exception
    {
        this.trustedAppsManager = trustedAppsManager;

        velocity = new VelocityEngine();
        velocity.addProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, JdkLogChute.class.getName());
        velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocity.init();
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        String action = request.getParameter("a");
        if (action == null)
        {
            action = Actions.list.name();
        }
        switch (Actions.valueOf(action))
        {
        case add:
            add(request, response);
            break;
        case list:
            list(request, response);
            break;
        case delete:
            delete(request, response);
            break;
        }
    }

    private void list(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html");
        render("/list.vm", Collections.<String, Object>singletonMap("trustedApplications",
            trustedAppsManager.getTrustedApplications()), request, response);
    }

    private void delete(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException
    {
        trustedAppsManager.deleteApplication(request.getParameter("id"));
        response.sendRedirect(request.getRequestURL().toString());
    }

    private void add(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html");
        final Map<String, Object> context = new HashMap<String, Object>();
        if (request.getParameterMap().containsKey("s"))
        {
            boolean succeeded = true;
            Application app = null;
            String url = request.getParameter("url");
            if (isBlank(url))
            {
                context.put("urlError", "url is required");
                succeeded = false;
            }
            else
            {
                try
                {
                    app = trustedAppsManager.getApplicationCertificate(url);
                }
                catch (RetrievalException e)
                {
                    context.put("urlError", e.getMessage());
                    succeeded = false;
                }
            }
            long certificateTimeout = 1000L;
            try
            {
                if (!isBlank(request.getParameter("timeout")))
                {
                    certificateTimeout = Long.parseLong(request.getParameter("timeout"));
                    if (certificateTimeout < 0L)
                    {
                        context.put("timeoutError", "timeout cannot be < 0");
                        succeeded = false;
                    }
                }
            }
            catch (NumberFormatException e)
            {
                context.put("timeoutError", "timeout is not a number");
                succeeded = false;
            }

            final String[] urlPatterns = isBlank(request.getParameter("urlPatterns")) ?
                    new String[]{} :
                    request.getParameter("urlPatterns").split(",");

            final String[] ipPatterns = isBlank(request.getParameter("ipPatterns")) ?
                    new String[]{} :
                    request.getParameter("ipPatterns").split(",");

            if (succeeded)
            {
                try
                {
                    trustedAppsManager.addTrustedApplication(app, RequestConditions
                            .builder()
                            .setCertificateTimeout(certificateTimeout)
                            .addURLPattern(urlPatterns)
                            .addIPPattern(ipPatterns)
                            .build());
                    response.sendRedirect(request.getRequestURL().toString());
                    return;
                }
                catch (IPAddressFormatException e)
                {
                    context.put("ipPatternsError", e.getMessage());
                }
            }
        }
        render("/add.vm", context, request, response);
    }

    private enum Actions
    {
        add, list, delete
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        doGet(request, response);
    }

    private Template getTemplate(final String templateName) throws ServletException
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

    private void render(final String templateName, final Map<String, Object> params, final HttpServletRequest request,
        final HttpServletResponse response) throws IOException, ServletException
    {
        Template template = getTemplate(templateName);
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, Object> param : params.entrySet())
        {
            context.put(param.getKey(), param.getValue());
        }
        context.put("request", request);
        template.merge(context, response.getWriter());
    }
}
