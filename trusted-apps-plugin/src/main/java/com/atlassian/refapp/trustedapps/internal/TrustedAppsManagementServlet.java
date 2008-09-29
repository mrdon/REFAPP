package com.atlassian.refapp.trustedapps.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.atlassian.refapp.trustedapps.RefAppTrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever.RetrievalException;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TrustedAppsManagementServlet extends HttpServlet
{
    private final VelocityEngine velocity;
    private final RefAppTrustedApplicationsManager trustedAppsManager;
    
    public TrustedAppsManagementServlet(RefAppTrustedApplicationsManager trustedAppsManager)
    {
        this.trustedAppsManager = trustedAppsManager;
        
        velocity = new VelocityEngine();
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
            if (url == null)
            {
                context.put("url.error", "url is required");
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
                    context.put("url.error", e.getMessage());
                    succeeded = false;
                }
            }
            String name = request.getParameter("name");
            if (name == null)
            {
                context.put("name.error", "name is required");
                succeeded = false;
            }
            long certificateTimeout = 1000;
            try
            {
                if (request.getParameter("timeout") != null)
                {
                    certificateTimeout = Long.parseLong(request.getParameter("timeout"));
                    if (certificateTimeout < 0)
                    {
                        context.put("timeout.error", "timeout cannot be < 0");
                        succeeded = false;
                    }
                }
            }
            catch (NumberFormatException e)
            {
                context.put("timeout.error", "timeout is not a number");
                succeeded = false;
            }
            
            Set<String> urlPatterns = new HashSet<String>();
            if (request.getParameterMap().containsKey("urlPatterns"))
            {
                urlPatterns.addAll(Arrays.asList(request.getParameter("urlPatterns").split(",")));
            }
            
            Set<String> ipPatterns = new HashSet<String>();
            if (request.getParameterMap().containsKey("ipPatterns"))
            {
                ipPatterns.addAll(Arrays.asList(request.getParameter("ipPatterns").split(",")));
            }

            if (succeeded)
            {
                trustedAppsManager.addTrustedApplication(app, name, certificateTimeout, urlPatterns, ipPatterns);
                redirectToList(request, response);
                return;
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
        response.addHeader("Location", request.getPathInfo());
    }

    private void sendResponse(HttpServletResponse response, int responseCode, String message, String contentType)
    {
        response.setStatus(responseCode);
        response.setContentType(contentType);
        try
        {
            if (message != null && message.trim().length() > 0)
                response.getWriter().write(message);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not write error message", e);
        }
    }
}
