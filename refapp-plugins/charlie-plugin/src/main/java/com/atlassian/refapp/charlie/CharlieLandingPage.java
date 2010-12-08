package com.atlassian.refapp.charlie;

import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.templaterenderer.TemplateRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since   v2.5.0
 */
public class CharlieLandingPage extends CharlieServlet
{
    public CharlieLandingPage(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer templateRenderer, WebInterfaceManager webInterfaceManager, CharlieStore store)
    {
        super(pluginSettingsFactory, templateRenderer, webInterfaceManager, store);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException
    {
        final String key = getCharlieKeyFromPath(req);
        final Map<String, Object> context = new HashMap<String, Object>();

        if (store.getCharlies().contains(key)) {
            context.put("projectKey", key);
            context.put("projectName", store.getCharlieName(key));
            render("/templates/charlie.vm", context, response);
        } else {
            response.sendError(404, "Charlie with key " + key + " does not exist.");
        }
    }

    private String getCharlieKeyFromPath(HttpServletRequest req)
    {
        final String[] elements = req.getPathInfo().split("/");
        return elements[elements.length - 1];
    }
}
