package com.atlassian.maven.plugins.refapp.lifecycle;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import com.atlassian.maven.plugins.refapp.MavenGoals;

/**
 *
 * @goal filter-plugin-descriptor
 */
public class FilterPluginDescriptorMojo extends AbstractMojo {

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenGoals goals = new MavenGoals(project, session, pluginManager, getLog());
        goals.filterPluginDescriptor();
    }
}
