package com.atlassian.refapp.sal.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.ApplicationProperties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * This implementation can be backed by a file on the file system.  If a file in the current working directory called
 * "com.atlassian.refapp.sal.pluginsettings.xml" exists (can be overridden with system property sal.com.atlassian.refapp.sal.pluginsettings.store) exists, it loads and
 * persists all plugin settings to and from this file.  If no file exists, plugin settings are purely in memory.
 */
public class RefimplPluginSettingsFactory implements PluginSettingsFactory
{
    private static final String CHARLIE_KEYS = "charlie.keys";

    private static final Logger log = Logger.getLogger(RefimplPluginSettingsFactory.class);
    private final Properties properties;
    private File pluginSettingsFile;
    private boolean useMemoryStore = false;

    public RefimplPluginSettingsFactory(ApplicationProperties applicationProperties)
    {
        properties = new Properties();

        // Check if the memory store is to be used instead of setting file.
        useMemoryStore = Boolean.valueOf(System.getProperty("sal.com.atlassian.refapp.sal.pluginsettings.usememorystore", "false"));

        if (useMemoryStore)
        {
            pluginSettingsFile = null;
            log.info("Using memory store for plugin settings");
        }
        // now try to read the pluginSettings file.
        else
        {
            // Maintain backwards compatibility, check the old location.
            File oldLocationFile = new File(System.getProperty("sal.com.atlassian.refapp.sal.pluginsettings.store", "com.atlassian.refapp.sal.pluginsettings.xml"));

            // Fallback to the standard mechanism if there's a problem with the old location.
            if (!oldLocationFile.exists() || !oldLocationFile.canRead())
            {
                // Use refapp home directory
                File dataDir = new File(applicationProperties.getHomeDirectory(), "data");
                try
                {
                    // create the data dir if one doesn't exist.
                    if (!dataDir.exists())
                    {
                        dataDir.mkdirs();
                    }

                    // look for the plugin setting file in new location.
                    File newLocationFile = new File(dataDir, "com.atlassian.refapp.sal.pluginsettings.xml");

                    // if it doesn't exist, we create a new one.
                    if (!newLocationFile.exists())
                    {
                        newLocationFile.createNewFile();
                    }

                    // point the setting file to the new location file.
                    pluginSettingsFile = newLocationFile;
                }
                catch (IOException ioe)
                {
                    useMemoryStore = true;
                    log.error("Error creating plugin settings properties, fallback to memory store", ioe);
                    return;
                }
            }
            else
            {
                // point the setting file to the old one.
                pluginSettingsFile = oldLocationFile;
                log.warn("Reading plugin settings from the old location. Please upgrade this to the new configuration layout.");
            }

            // now load the setting file if it's not empty.
            if (pluginSettingsFile.length() > 0)
            {
                // even at this stage, the file might be readable but corrupted.
                pluginSettingsFile = load(pluginSettingsFile);

                if (pluginSettingsFile != null)
                {
                    log.info("Using " + pluginSettingsFile.getAbsolutePath() + " as plugin settings store");
                }
            }
        }
    }

    private File load(File file)
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream(file);
            properties.loadFromXML(is);
        }
        catch (Exception e)
        {
            log.error("Error loading plugin settings properties, using memory store", e);
            file = null;
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException ioe)
                {
                    log.error("Error closing file", ioe);
                }
            }
        }
        return file;
    }

    public PluginSettings createSettingsForKey(String key)
    {
        if (key != null)
        {
            List<String> charlies = (List<String>) new RefimplPluginSettings(new SettingsMap(null)).get(CHARLIE_KEYS);
            if (charlies == null || !charlies.contains(key))
            {
                throw new IllegalArgumentException("No Charlie with key " + key + " exists.");
            }
        }
        return new RefimplPluginSettings(new SettingsMap(key));
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }

    @SuppressWarnings("AccessToStaticFieldLockedOnInstance")
    private synchronized void store()
    {
        if (useMemoryStore)
        {
            // No need to store anything to disk.
            return;
        }
        else if (!pluginSettingsFile.canWrite())
        {
            log.error("Cannot store settings into file.");
        }

        OutputStream os = null;
        try
        {
            os = new FileOutputStream(pluginSettingsFile);
            properties.storeToXML(os, "SAL Reference Implementation plugin settings");
        }
        catch (IOException ioe)
        {
            log.error("Error storing properties", ioe);
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException ioe)
                {
                    log.error("Error closing output stream", ioe);
                }
            }
        }
    }

    private class SettingsMap extends AbstractMap<String, String>
    {
        private final String settingsKey;

        private SettingsMap(String settingsKey)
        {
            if (settingsKey == null)
            {
                this.settingsKey = "global.";
            }
            else
            {
                this.settingsKey = "keys." + settingsKey + ".";
            }
        }

        public Set<Entry<String, String>> entrySet()
        {
            Set<Entry<String, String>> set = new HashSet<Entry<String, String>>();

            for(Entry entry:properties.entrySet())
            {
                set.add(entry);
            }

            return set;
        }

        public String get(Object key)
        {
            return properties.getProperty(settingsKey + key);
        }

        public String put(String key, String value)
        {
            String result = (String) properties.setProperty(settingsKey + key, value);
            store();
            return result;
        }

        public String remove(Object key)
        {
            String result = (String) properties.remove(settingsKey + key);
            store();
            return result;
        }
    }
}
