package com.atlassian.maven.plugins.refapp.pdk;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;

/**
 *
 */
public abstract class AbstractPdkMojo extends AbstractMojo {
    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    /**
     * The Maven Session Object
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;
    /**
     * The Maven PluginManager Object
     *
     * @component
     * @required
     */
    protected PluginManager pluginManager;
    /**
     * @parameter expression="${atlassian.plugin.key}"
    */
   protected String pluginKey;
    /**
     * @parameter expression="${project.groupId}"
    */
   protected String groupId;
    /**
     * @parameter expression="${project.artifactId}"
    */
   protected String artifactId;
    /**
     * HTTP port for the servlet containers
    *
    * @parameter expression="${http.port}"
    */
   protected int httpPort = 9400;


    protected void ensurePluginKeyExists() {
        if (pluginKey == null)
        {
            pluginKey = groupId + "." + artifactId;
        }
    }
}
