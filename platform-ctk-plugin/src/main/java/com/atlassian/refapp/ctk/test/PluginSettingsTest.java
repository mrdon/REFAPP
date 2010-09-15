package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class PluginSettingsTest extends SpringAwareTestCase
{
    private static final String LONG_STRING = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    private PluginSettingsFactory factory;

    public void setFactory(PluginSettingsFactory factory)
    {
        this.factory = factory;
    }

    @Test
    public void testInjection()
    {
        assertTrue("PluginSettingsFactory should be injectable", factory != null);
    }

    @Test
    @SuppressWarnings("unchecked")
	public void testSaveAndLoadSettings()
    {
        final PluginSettings settings = factory.createGlobalSettings();
        assertTrue("Global PluginSettings should be retrievable", settings != null);

        settings.put("string", "foo");
        assertTrue("Should be able to store and retrieve a string", "foo".equals(settings.get("string")));

        settings.put("longstring", LONG_STRING);
        assertTrue("Should be able to store and retrieve a string", LONG_STRING.equals(settings.get("longstring")));

        List<String> list = Arrays.asList("foo");
        settings.put("list", list);
        list = (List<String>) settings.get("list");
        assertTrue("Should be able to store and retrieve a list", list != null && "foo".equals(list.get(0)));

        Properties map = new Properties();
        map.setProperty("key", "value");
        settings.put("map", map);
        map = (Properties) settings.get("map");
        assertTrue("Should be able to store and retrieve a map", map != null && "value".equals(map.get("key")));

        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "value");
        settings.put("hashMap", hashMap);
        hashMap = (Map) settings.get("hashMap");
        assertTrue("Should be able to store and retrieve a real map", hashMap != null && "value".equals(hashMap.get("key")));
    }
}