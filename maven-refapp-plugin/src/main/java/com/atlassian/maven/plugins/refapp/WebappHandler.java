package com.atlassian.maven.plugins.refapp;

import org.apache.maven.project.MavenProject;

import java.util.Map;
import java.util.Collection;
import java.io.File;

/**
 * Handler for webapp-specific settings
 */
public interface WebappHandler
{
    /**
     * @return the webapp id
     */
    String getId();

    /**
     * @return the webapp maven group id
     */
    String getGroupId();

    /**
     * @return the webapp maven artifact id
     */
    String getArtifactId();

    /**
     * @return the webapp version
     */
    String getVersion();

    /**
     * @param project The current project
     * @return the system properties to set when executing the webapp
     */
    Map<String,String> getSystemProperties(MavenProject project);

    /**
     * @param salVersion The sal version
     * @return the list of artifacts to include when the sal version is specified
     */
    Collection<WebappArtifact> getSalArtifacts(String salVersion);

    /**
     * @param webappDir the webapp directory
     * @return the directory to store plugins into.  Return null to force plugins into the bundled plugins zip.
     */
    File getPluginsDirectory(String webappDir);

    /**
     * @return the path to the bundled plugins zip within the webapp
     */
    String getBundledPluginPath();
}
