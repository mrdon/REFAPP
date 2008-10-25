package com.atlassian.refapp.trustedapps.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang.StringUtils.isBlank;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.atlassian.refapp.trustedapps.RefAppTrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever.RetrievalException;
import com.atlassian.security.auth.trustedapps.IPAddressFormatException;


public class TrustedAppsManagementServlet extends HttpServlet
{
    private final VelocityEngine velocity;
    private final RefAppTrustedApplicationsManager trustedAppsManager;
    
    public TrustedAppsManagementServlet(RefAppTrustedApplicationsManager trustedAppsManager) throws Exception
    {
        this.trustedAppsManager = trustedAppsManager;
        
        velocity = new VelocityEngine();
        velocity.addProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, JdkLogChute.class.getName());
        velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());        
        velocity.init();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        render(
            "/list.vm",
            Collections.<String, Object>singletonMap("trustedApplications", trustedAppsManager.getTrustedApplications()),
            request,
            response
        );
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        Map<String, Object> context = new HashMap<String, Object>();
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
            long certificateTimeout = 1000;
            try
            {
                if (!isBlank(request.getParameter("timeout")))
                {
                    certificateTimeout = Long.parseLong(request.getParameter("timeout"));
                    if (certificateTimeout < 0)
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
            
            Set<String> urlPatterns = new HashSet<String>();
            if (!isBlank(request.getParameter("urlPatterns")))
            {
                urlPatterns.addAll(Arrays.asList(request.getParameter("urlPatterns").split(",")));
            }
            
            Set<String> ipPatterns = new HashSet<String>();
            if (!isBlank(request.getParameter("ipPatterns")))
            {
                ipPatterns.addAll(Arrays.asList(request.getParameter("ipPatterns").split(",")));
            }

            if (succeeded)
            {
                try
                {
                    trustedAppsManager.addTrustedApplication(app, certificateTimeout, urlPatterns, ipPatterns);
                    redirectToList(request, response);
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
        add, list
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
    
    private void render(String templateName, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
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
    
    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
    {
        response.addHeader("Location", request.getContextPath() + "/plugins/servlet" + request.getPathInfo());
        response.setStatus(301);
    }
}
