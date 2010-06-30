package com.atlassian.sal.refimpl.message;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Enumeration;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.core.message.AbstractI18nResolver;

/**
 * Returns the key with args as a string
 */
public class RefimplI18nResolver extends AbstractI18nResolver
{
    private final Map<Plugin, Iterable<String>> pluginResourceBundleNames =
        new HashMap<Plugin, Iterable<String>>();

    private final ResourceBundleResolver resolver;

    public RefimplI18nResolver(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager,
                               ResourceBundleResolver resolver)
    {
        pluginEventManager.register(this);
        addPluginResourceBundles(pluginAccessor.getPlugins());
        this.resolver = assertNotNull(resolver, "resolver");
    }

    public String resolveText(String key, Serializable[] arguments)
    {
        String message = null;
        for (Plugin plugin : pluginResourceBundleNames.keySet())
        {
            for (String bundleName : pluginResourceBundleNames.get(plugin))
            {
                try
                {
                    ResourceBundle bundle = getBundle(bundleName, Locale.getDefault(), plugin);
                    message = MessageFormat.format(bundle.getString(key), (Object[]) arguments);
                }
                catch (MissingResourceException e)
                {
                    // ignore, try next bundle
                }
            }
        }
        if (message == null)
        {
            message = key;
        }
        return message;
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix)
    {
        assertNotNull(prefix, "prefix");

        Map<String, String> translationsWithPrefix = new HashMap<String, String>();
        for (Plugin plugin : pluginResourceBundleNames.keySet())
        {

            addMatchingTranslationsToMap(prefix, Locale.getDefault(), plugin, pluginResourceBundleNames.get(plugin),
                                         translationsWithPrefix);
        }
        return translationsWithPrefix;
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale)
    {
        assertNotNull(prefix, "prefix");
        assertNotNull(locale, "locale");

        Map<String, String> translationsWithPrefix = new HashMap<String, String>();
        for (Plugin plugin : pluginResourceBundleNames.keySet())
        {

            addMatchingTranslationsToMap(prefix, locale, plugin, pluginResourceBundleNames.get(plugin),
                                         translationsWithPrefix);
        }
        return translationsWithPrefix;
    }

    private void addMatchingTranslationsToMap(String prefix, Locale locale, Plugin plugin,
                                              Iterable<String> bundleNames,
                                              Map<String, String> translationsWithPrefix)
    {
        for (String bundleName : bundleNames)
        {
            try
            {
                ResourceBundle bundle = getBundle(bundleName, locale, plugin);
                if (bundle != null)
                {
                    addMatchingTranslationsToMap(prefix, bundle, translationsWithPrefix);
                }
            }
            catch (MissingResourceException e)
            {
                // OK, just ignore
            }
        }
    }

    private void addMatchingTranslationsToMap(String prefix, ResourceBundle bundle,
                                              Map<String, String> translationsWithPrefix)
    {
        Enumeration enumeration = bundle.getKeys();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            if (key.startsWith(prefix))
            {
                translationsWithPrefix.put(key, bundle.getString(key));
            }
        }
    }

    @PluginEventListener
    public void pluginEnabled(PluginEnabledEvent event)
    {
        addPluginResourceBundles(event.getPlugin());
    }

    @PluginEventListener
    public void pluginDisabled(PluginDisabledEvent event)
    {
        removePluginResourceBundles(event.getPlugin());
    }

    private void addPluginResourceBundles(Iterable<Plugin> plugins)
    {
        for (Plugin plugin : plugins)
        {
            addPluginResourceBundles(plugin);
        }
    }

    private void addPluginResourceBundles(Plugin plugin)
    {
        List<String> bundleNames = new LinkedList<String>();
        Iterable<ResourceDescriptor> descriptors = plugin.getResourceDescriptors("i18n");
        for (ResourceDescriptor descriptor : descriptors)
        {
            bundleNames.add(descriptor.getLocation());
        }
        addPluginResourceBundles(plugin, bundleNames);
    }

    private void addPluginResourceBundles(Plugin plugin, List<String> bundleNames)
    {
        pluginResourceBundleNames.put(plugin, bundleNames);
    }

    private void removePluginResourceBundles(Plugin plugin)
    {
        pluginResourceBundleNames.remove(plugin);
    }

    private ResourceBundle getBundle(String bundleName, Locale locale, Plugin plugin)
    {
        return resolver.getBundle(bundleName, locale, plugin.getClassLoader());
    }

    private static <T> T assertNotNull(T object, String name)
    {
        if (object == null)
        {
            throw new NullPointerException(name + " must not be null");
        }
        return object;
    }
}
