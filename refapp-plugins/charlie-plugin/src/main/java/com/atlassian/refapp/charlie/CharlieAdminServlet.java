package com.atlassian.refapp.charlie;

import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

public class CharlieAdminServlet extends CharlieServlet
{
    public CharlieAdminServlet(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer templateRenderer, WebInterfaceManager webInterfaceManager)
    {
        super(pluginSettingsFactory, templateRenderer, webInterfaceManager);
    }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final Map<String, Object> context = new HashMap<String, Object>();
        final String delete = request.getParameter("delete");
        if (delete == null)
        {
            final Map<String, String> charlies = new HashMap<String, String>();
            for (String charlie : getCharlies())
            {
                charlies.put(charlie, getCharlieName(charlie));
            }
            context.put("charlies", charlies);
            render("/templates/charliesadmin.vm", context, response);
        }
        else
        {
            final List<String> charlies = getCharlies();
            charlies.remove(delete);
            storeCharlies(charlies);
            response.sendRedirect(request.getRequestURL().toString());
        }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final String key = request.getParameter("key");
        final String name = request.getParameter("name");
        final List<String> charlies = getCharlies();
        charlies.add(key);
        storeCharlies(charlies);
        setCharlieName(key, name);
        response.sendRedirect(request.getRequestURL().toString());
    }
}
