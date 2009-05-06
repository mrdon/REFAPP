package com.atlassian.maven.plugins.refapp.cli;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import com.atlassian.maven.plugins.refapp.MavenGoals;
import com.atlassian.maven.plugins.refapp.WebappHandler;
import com.atlassian.maven.plugins.refapp.RefappWebappHandler;
import com.atlassian.maven.plugins.refapp.MavenContext;

/**
 * @goal idea
 */
public class IdeaMojo extends AbstractMojo {

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

    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenGoals goals = new MavenGoals(new MavenContext(project, session, pluginManager, getLog()), getWebappHandler());
        goals.installIdeaPlugin();
    }
    
    protected WebappHandler getWebappHandler()
    {
         return new RefappWebappHandler();
    }
}