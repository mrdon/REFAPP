package com.atlassian.refapp.charlie;

import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.templaterenderer.TemplateRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.google.common.base.Preconditions.checkNotNull;

public class CharlieAdminServlet extends CharlieServlet
{

    private final WebSudoManager webSudoManager;

    public CharlieAdminServlet(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer templateRenderer,
                               WebInterfaceManager webInterfaceManager, final WebSudoManager webSudoManager,
                               CharlieStore store)
    {
        super(pluginSettingsFactory, templateRenderer, webInterfaceManager, store);
        this.webSudoManager = checkNotNull(webSudoManager);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        try
        {
            webSudoManager.willExecuteWebSudoRequest(request);
            
            final Map<String, Object> context = new HashMap<String, Object>();
            final String delete = request.getParameter("delete");
            if (delete == null)
            {
                final Map<String, String> charlies = new HashMap<String, String>();
                for (String charlie : store.getCharlies())
                {
                    charlies.put(charlie, store.getCharlieName(charlie));
                }
                context.put("charlies", charlies);
                render("/templates/charliesadmin.vm", context, response);
            }
            else
            {
                final List<String> charlies = store.getCharlies();
                charlies.remove(delete);
                store.storeCharlies(charlies);
                response.sendRedirect(request.getRequestURL().toString());
            }
        } catch (WebSudoSessionException wse)
        {
            webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        try
        {
            webSudoManager.willExecuteWebSudoRequest(request);

            final String key = request.getParameter("key");
            final String name = request.getParameter("name");
            final List<String> charlies = store.getCharlies();
            charlies.add(key);
            store.storeCharlies(charlies);
            store.setCharlieName(key, name);
            response.sendRedirect(request.getRequestURL().toString());
        } catch (WebSudoSessionException wse)
        {
            // Instead of enforcing WebSudo protection this could log an error or throw an exception...
            webSudoManager.enforceWebSudoProtection(request, response);
        }
    }
}
