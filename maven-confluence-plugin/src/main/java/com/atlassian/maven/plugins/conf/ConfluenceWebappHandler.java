package com.atlassian.maven.plugins.conf;

import com.atlassian.maven.plugins.refapp.WebappHandler;
import com.atlassian.maven.plugins.refapp.WebappArtifact;
import com.atlassian.maven.plugins.refapp.MavenContext;

import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.io.File;

import org.apache.maven.project.MavenProject;

public class ConfluenceWebappHandler implements WebappHandler
{
    public String getId()
    {
        return "confluence";
    }

    public String getGroupId()
    {
        return "com.atlassian.confluence";
    }

    public String getArtifactId()
    {
        return "confluence-webapp";
    }

    public Map<String, String> getSystemProperties(MavenProject project)
    {
        return Collections.singletonMap("confluence.home", getHomeDirectory(project).getPath());
    }

    public Collection<WebappArtifact> getSalArtifacts(String salVersion)
    {
        return Arrays.asList(
                new WebappArtifact("com.atlassian.sal", "sal-api", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-confluence-plugin", salVersion));
    }

    public File getPluginsDirectory(String webappDir)
    {
        // indicates plugins should be bundled
        return null;
    }

    public String getBundledPluginPath()
    {
        return "WEB-INF/classes/com/atlassian/confluence/setup/atlassian-bundled-plugins.zip";
    }

    public String getVersion()
    {
        return "RELEASE";
    }

    public File getHomeDirectory(MavenProject project)
    {
        return new File(project.getBuild().getDirectory(), "confluence-home");
    }
}
