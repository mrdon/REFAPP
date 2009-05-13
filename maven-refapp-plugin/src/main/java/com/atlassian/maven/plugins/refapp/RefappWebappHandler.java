package com.atlassian.maven.plugins.refapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

public class RefappWebappHandler implements WebappHandler
{
    public String getId()
    {
        return "refapp";
    }

    public WebappArtifact getArtifact()
    {
        return new WebappArtifact("com.atlassian.refapp", "atlassian-refapp", getVersion());
    }

    public WebappArtifact getTestResourcesArtifact()
    {
        return null;
    }

    public Map<String, String> getSystemProperties(final MavenProject project)
    {
        return Collections.singletonMap("osgi.cache", "${project.build.directory}/osgi-cache");
    }

    public Collection<WebappArtifact> getSalArtifacts(final String salVersion)
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

    public File getPluginsDirectory(final String webappDir, File homeDir)
    {
        return new File(webappDir, "WEB-INF/plugins");
    }

    public List<WebappArtifact> getExtraContainerDependencies()
    {
        return Collections.emptyList();
    }

    public String getBundledPluginPath()
    {
        return "WEB-INF/classes/atlassian-bundled-plugins.zip";
    }

    public void processHomeDirectory(MavenProject project, File homeDir, AbstractWebappMojo webappMojo) throws MojoExecutionException
    {
    }

    private String getVersion()
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
