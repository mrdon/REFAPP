package com.atlassian.sal.refimpl.project;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class RefimplProjectManager implements com.atlassian.sal.api.project.ProjectManager
{
    private static final String CHARLIE_KEYS = "charlie.keys";

    private final PluginSettingsFactory pluginSettingsFactory;

    public RefimplProjectManager(PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    /**
     * Get all project keys
     *
     * @return All the project keys
     */
    public Collection<String> getAllProjectKeys()
    {
        List<String> charlies = (List<String>) pluginSettingsFactory.createGlobalSettings().get(CHARLIE_KEYS);
        if (charlies == null)
        {
            charlies = new ArrayList<String>();
        }
        return charlies;
    }

}
