package com.atlassian.refapp.sal.pluginsettings;

import com.atlassian.sal.core.pluginsettings.AbstractStringPluginSettings;

import java.util.Map;

public class RefimplPluginSettings extends AbstractStringPluginSettings
{
    private final Map<String,String> map;
    public RefimplPluginSettings(Map<String, String> map)
    {
        this.map = map;
    }

    protected void putActual(String key, String val)
    {
        map.put(key, XmlUtils.escape(val));
    }

    protected String getActual(String key)
    {
        return XmlUtils.unescape(map.get(key));
    }

    protected void removeActual(String key)
    {
        map.remove(key);
    }
}
