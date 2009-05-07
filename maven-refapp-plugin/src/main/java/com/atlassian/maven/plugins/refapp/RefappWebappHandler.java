package com.atlassian.maven.plugins.refapp;

import org.apache.commons.io.IOUtils;
import org.apache.maven.project.MavenProject;

import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class RefappWebappHandler implements WebappHandler
{
    public String getId()
    {
        return "refapp";
    }

    public String getGroupId()
    {
        return "com.atlassian.refapp";
    }

    public String getArtifactId()
    {
        return "atlassian-refapp";
    }

    public Map<String, String> getSystemProperties(MavenProject project)
    {
        return Collections.singletonMap("osgi.cache", "${project.build.directory}/osgi-cache");
    }

    public Collection<WebappArtifact> getSalArtifacts(String salVersion)
    {
        return Arrays.asList(
                new WebappArtifact("com.atlassian.sal", "sal-api", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-appproperties-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-component-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-executor-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-lifecycle-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-message-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-net-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-pluginsettings-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-project-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-search-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-transaction-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-upgrade-plugin", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-refimpl-user-plugin", salVersion));
    }

    public File getPluginsDirectory(String webappDir)
    {
        return new File(webappDir, "WEB-INF/plugins");
    }

    public String getBundledPluginPath()
    {
        return "WEB-INF/classes/atlassian-bundled-plugins.zip";
    }

    public String getVersion()
    {
        InputStream in = null;
        final Properties props = new Properties();
        try
        {
            in = getClass().getClassLoader()
                    .getResourceAsStream(
                            "META-INF/maven/com.atlassian.maven.plugins/maven-refapp-plugin/pom.properties");
            if (in != null)
            {
                props.load(in);
                return props.getProperty("version");
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        return null;
    }

}
