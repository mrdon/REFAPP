package com.atlassian.refapp.sal.message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class RefimplI18nResolverTest
{
    private static final String PREFIX = "com.atlassian.sal.com.atlassian.refapp.sal.message.test.";

    private static final String KEY_WITH_PREFIX_AT_START_1 = PREFIX + "key1";
    private static final String KEY_WITHOUT_PREFIX_1 = "noprefix.key";
    private static final String KEY_WITH_PREFIX_NOT_AT_START_1 = "notatstart." + PREFIX + "key";
    private static final String KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1 = PREFIX + "onlyinfrench.key";

    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1 = "Hello, world";
    private static final String FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1 = "Bonjour tout le monde";
    private static final String US_VALUE_FOR_KEY_WITHOUT_PREFIX_1 = "Hello, Atlassian";
    private static final String FR_VALUE_FOR_KEY_WITHOUT_PREFIX_1 = "Bonjour, Atlassian";
    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1 = "Hello, San Francisco";
    private static final String FR_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1 = "Bonjour, San Francisco";
    private static final String FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1 = "Bonjour, France!";

    private static final String KEY_WITHOUT_PREFIX_2 = "another." + KEY_WITHOUT_PREFIX_1;
    private static final String KEY_WITH_PREFIX_NOT_AT_START_2 = "another.notatstart." + PREFIX + "key";
    private static final String KEY_WITH_PREFIX_AT_START_2 = PREFIX + "key2";

    private static final String US_VALUE_FOR_KEY_WITHOUT_PREFIX_2 = "Hello, universe";
    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_2 = "Hello, Sydney";
    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2 = "Hello, everyone";

    private static final String KEY_3 = "key3";
    private static final String VALUE_FOR_KEY_3 = "Hello {0}";

    private static final ResourceBundle bundle1_en_US = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.US;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1},
                    {KEY_WITHOUT_PREFIX_1, US_VALUE_FOR_KEY_WITHOUT_PREFIX_1},
                    {KEY_WITH_PREFIX_NOT_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1}
                };
        }
    };

    private static final ResourceBundle bundle2_en_US = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.US;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_WITHOUT_PREFIX_2, US_VALUE_FOR_KEY_WITHOUT_PREFIX_2},
                    {KEY_WITH_PREFIX_NOT_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_2},
                    {KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2}
                };
        }
    };

    private static final ResourceBundle bundle1_fr_FR = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.FRANCE;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_WITH_PREFIX_AT_START_1, FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1},
                    {KEY_WITHOUT_PREFIX_1, FR_VALUE_FOR_KEY_WITHOUT_PREFIX_1},
                    {KEY_WITH_PREFIX_NOT_AT_START_1, FR_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1},
                    {KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1, FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1}
                };
        }
    };

    private static final ResourceBundle bundle3_currentLocale = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.getDefault();
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_3, VALUE_FOR_KEY_3}
                };
        }
    };

    private static final ResourceBundleResolver resourceBundleResolver = new ResourceBundleResolver()
    {
        public ResourceBundle getBundle(String baseName, Locale locale, ClassLoader classLoader)
        {
            if (baseName.equals("bundle1"))
            {
                if (locale.equals(bundle1_en_US.getLocale()))
                {
                    return bundle1_en_US;
                }
                else if (locale.equals(bundle1_fr_FR.getLocale()))
                {
                    return bundle1_fr_FR;
                }
            }
            else if (baseName.equals("bundle2"))
            {
                if (locale.equals(bundle2_en_US.getLocale()))
                {
                    return bundle2_en_US;
                }
            }
            else if (baseName.equals("bundle3"))
            {
                if (locale.equals(bundle3_currentLocale.getLocale()))
                {
                    return bundle3_currentLocale;
                }
            }
            throw new MissingResourceException("Can't find bundle for base name " + baseName + ", locale " + locale,
                                               baseName + "_" + locale,"");
        }
    };

    private final PluginAccessor pluginAccessor = Mockito.mock(PluginAccessor.class);
    private final PluginEventManager pluginEventManager = Mockito.mock(PluginEventManager.class);
    private final Plugin plugin = Mockito.mock(Plugin.class);
    private final ResourceDescriptor bundleResourceDescriptor1 = mockResourceDescriptor("bundle1");
    private final ResourceDescriptor bundleResourceDescriptor2 = mockResourceDescriptor("bundle2");
    private final ResourceDescriptor bundleResourceDescriptor3 = mockResourceDescriptor("bundle3");

    private RefimplI18nResolver resolver =
        new RefimplI18nResolver(pluginAccessor, pluginEventManager, resourceBundleResolver);

    @Test
    public void getAllTranslationsForPrefixWithNoResourceBundlesAvailableReturnsEmptyMap()
    {
        Assert.assertTrue(resolver.getAllTranslationsForPrefix(PREFIX, Locale.US).isEmpty());
    }

    @Test
    public void getAllTranslationsForPrefixWithMatchingPrefixAndLocaleReturnsMatches()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor1));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix(PREFIX, Locale.US));
    }

    @Test
    public void getAllTranslationsForPrefixWithMatchingPrefixAndNonMatchingLocaleReturnsEmptyMap()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor1));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));

        Assert.assertTrue(resolver.getAllTranslationsForPrefix(PREFIX, Locale.GERMANY).isEmpty());
    }

    @Test
    public void getAllTranslationsForPrefixWithNonMatchingPrefixAndMatchingLocaleReturnsEmptyMap()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor1));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));

        Assert.assertTrue(resolver.getAllTranslationsForPrefix(PREFIX + "nomatch", Locale.US).isEmpty());
    }

    @Test
    public void getAllTranslationsForPrefixWithMultipleMatchingBundlesInOnePluginReturnsAllMatches()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n"))
            .thenReturn(Arrays.asList(bundleResourceDescriptor1, bundleResourceDescriptor2));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix(PREFIX, Locale.US));
    }

    @Test
    public void getAllTranslationsForPrefixWithMultipleMatchingBundlesInDifferentPluginsReturnsAllMatches()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor1));
        Plugin plugin2 = Mockito.mock(Plugin.class);
        Mockito.when(plugin2.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor2));

        resolver.pluginEnabled(new PluginEnabledEvent(plugin));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin2));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix(PREFIX, Locale.US));
    }

    @Test(expected = NullPointerException.class)
    public void getAllTranslationsWithNullPrefixThrowsNullPointerException()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor1));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));
        resolver.getAllTranslationsForPrefix(null, Locale.US);
    }

    @Test
    public void getAllTranslationsWithEmptyPrefixReturnsAllTranslationsInMatchingLocales()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n"))
            .thenReturn(Arrays.asList(bundleResourceDescriptor1, bundleResourceDescriptor2));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITHOUT_PREFIX_1, US_VALUE_FOR_KEY_WITHOUT_PREFIX_1);
        expectations.put(KEY_WITH_PREFIX_NOT_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1);
        expectations.put(KEY_WITHOUT_PREFIX_2, US_VALUE_FOR_KEY_WITHOUT_PREFIX_2);
        expectations.put(KEY_WITH_PREFIX_NOT_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_2);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix("", Locale.US));
    }

    @Test(expected = NullPointerException.class)
    public void getAllTranslationsWithNullLocaleThrowsNullPointerException()
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor1));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));
        resolver.getAllTranslationsForPrefix(PREFIX, null);
    }

    @Test
    public void resolveText() throws Exception
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor3));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));

        assertEquals("Hello World", resolver.resolveText(KEY_3, new Serializable[] { "World" }));
        assertEquals(KEY_WITHOUT_PREFIX_2, resolver.resolveText(KEY_WITHOUT_PREFIX_2, new Serializable[0]));
    }

    @Test
    public void getRawText() throws Exception
    {
        Mockito.when(plugin.getResourceDescriptors("i18n")).thenReturn(Arrays.asList(bundleResourceDescriptor3));
        resolver.pluginEnabled(new PluginEnabledEvent(plugin));

        assertEquals(VALUE_FOR_KEY_3, resolver.getRawText(KEY_3));
        assertEquals(KEY_WITHOUT_PREFIX_2, resolver.resolveText(KEY_WITHOUT_PREFIX_2, new Serializable[0]));
    }

    private static ResourceDescriptor mockResourceDescriptor(String location)
    {
        ResourceDescriptor resourceDescriptor = Mockito.mock(ResourceDescriptor.class);
        Mockito.when(resourceDescriptor.getLocation()).thenReturn(location);
        return resourceDescriptor;
    }
}
