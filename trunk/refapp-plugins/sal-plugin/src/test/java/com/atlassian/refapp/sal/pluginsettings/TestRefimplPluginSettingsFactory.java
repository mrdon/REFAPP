package com.atlassian.refapp.sal.pluginsettings;

import com.atlassian.sal.api.ApplicationProperties;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class TestRefimplPluginSettingsFactory extends TestCase
{
    private File tmpDir;

    @Override
    protected void setUp() throws Exception
    {
        tmpDir = new File("target/plugin-temp").getAbsoluteFile();
        if (tmpDir.exists())
        {
            FileUtils.cleanDirectory(tmpDir);
        }
        tmpDir.mkdirs();
    }

    public void testFilePersistence()
    {
        ApplicationProperties props = Mockito.mock(ApplicationProperties.class);
        Mockito.when(props.getHomeDirectory()).thenReturn(tmpDir);
        RefimplPluginSettingsFactory factory = new RefimplPluginSettingsFactory(props);
        RefimplPluginSettings settings = (RefimplPluginSettings) factory.createGlobalSettings();
        settings.put("foo", "bar");

        factory = new RefimplPluginSettingsFactory(props);
        assertEquals("bar", factory.createGlobalSettings().get("foo"));
    }

    public void testFilePersistenceWithNewlinesAndPipes()
    {
        ApplicationProperties props = Mockito.mock(ApplicationProperties.class);
        Mockito.when(props.getHomeDirectory()).thenReturn(tmpDir);
        RefimplPluginSettingsFactory factory = new RefimplPluginSettingsFactory(props);
        RefimplPluginSettings settings = (RefimplPluginSettings) factory.createGlobalSettings();
        settings.put("foo", "bar\n|baz");

        factory = new RefimplPluginSettingsFactory(props);
        assertEquals("bar\n|baz", factory.createGlobalSettings().get("foo"));
    }
}
