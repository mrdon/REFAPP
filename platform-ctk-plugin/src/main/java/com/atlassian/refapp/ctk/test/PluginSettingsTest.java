package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PluginSettingsTest extends SpringAwareTestCase
{
    private static final String VERY_LONG_STRING = StringUtils.repeat("ABCDEFGHIJ", 10000);

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
	public void testSaveString()
    {
        final PluginSettings settings = factory.createGlobalSettings();
        assertTrue("Global PluginSettings should be retrievable", settings != null);

        settings.put("string", "foo");
        assertTrue("Should be able to store and retrieve a string", "foo".equals(settings.get("string")));

        settings.put("longstring", VERY_LONG_STRING);
        assertTrue("Should be able to store and retrieve a string", VERY_LONG_STRING.equals(settings.get("longstring")));
    }

    @Test
    public void testSaveList()
    {
        final PluginSettings settings = factory.createGlobalSettings();
        List<String> oldList = Arrays.asList("foo", "faa", "fee", "fuu");
        settings.put("list", oldList);

        List<String> list = (List<String>) settings.get("list");
        assertNotNull("Should be able to store and retrieve a list", list);
        assertEquals("Should be able to store and retrieve a list", list, oldList);
    }

    @Test
    public void testSaveProperties()
    {
        final PluginSettings settings = factory.createGlobalSettings();
        Properties oldProp = new Properties();
        oldProp.setProperty("key1", "value1");
        oldProp.setProperty("key2", "value2");
        settings.put("prop", oldProp);

        Properties prop = (Properties) settings.get("prop");
        assertNotNull("Should be able to store and retrieve a map", prop);
        assertTrue("Should be able to store and retrieve a map", oldProp.equals(prop));
    }

    @Test
    public void testSaveMap()
    {
        final PluginSettings settings = factory.createGlobalSettings();
        Map<String, String> oldMap = new HashMap<String, String>();
        oldMap.put("key1", "value1");
        oldMap.put("key2", "value2");
        settings.put("map", oldMap);

        Map<String, String> map = (Map) settings.get("map");
        assertNotNull("Should be able to store and retrieve a map", map);
        assertTrue("Should be able to store and retrieve a map", map.equals(oldMap));
    }

    @Test
    public void testSpecialCharacters()
    {
        final PluginSettings settings = factory.createGlobalSettings();
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key\n\t\f\r", "value\n\t\f\r");
        settings.put("hashMap", hashMap);
        hashMap = (Map) settings.get("hashMap");
        assertTrue("Should be able to store and retrieve a real map", hashMap != null && "value\n\t\f\r".equals(hashMap.get("key\n\t\f\r")));
    }
}
