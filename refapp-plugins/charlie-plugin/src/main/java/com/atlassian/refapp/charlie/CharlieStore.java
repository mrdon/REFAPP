package com.atlassian.refapp.charlie;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Store for Charlies.
 */
public class CharlieStore
{
    private static final String CHARLIE_KEYS = "charlie.keys";
    private static final String CHARLIE_NAME = "charlie.name";

    private final PluginSettingsFactory pluginSettingsFactory;

    public CharlieStore(PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    List<String> getCharlies()
    {
        List<String> charlies = (List<String>) pluginSettingsFactory.createGlobalSettings().get(CHARLIE_KEYS);
        if (charlies == null)
        {
            charlies = new ArrayList<String>();
        }
        return charlies;
    }

    void storeCharlies(List<String> charlies)
    {
        pluginSettingsFactory.createGlobalSettings().put(CHARLIE_KEYS, charlies);
    }

    String getCharlieName(String key)
    {
        return (String) pluginSettingsFactory.createSettingsForKey(key).get(CHARLIE_NAME);
    }

    void setCharlieName(String key, String name)
    {
        pluginSettingsFactory.createSettingsForKey(key).put(CHARLIE_NAME, name);
    }
}
