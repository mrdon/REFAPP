package com.atlassian.maven.plugins.refapp;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;

/**
 * Run the refapp
 *
 * @requiresDependencyResolution run
 * @goal run
 */
public class RunMojo
        extends AbstractRefappMojo
{
    public void execute()
            throws MojoExecutionException
    {
        MavenGoals goals = new MavenGoals(project, session, pluginManager, getLog());

        // Copy the refapp war to target
        File refappWar = goals.copyRefappWar(determineVersion());

        File combinedRefappWar = addPlugins(goals, refappWar);

        // Start the refapp
        goals.startRefapp(combinedRefappWar, containerId, httpPort, jvmArgs);

        getLog().info("Refapp started successfully and available at http://localhost:"+httpPort+"/refapp");
        getLog().info("Type any key to exit");
        try {
            System.in.read();
        } catch (IOException e) {
            // ignore
        }
    }
}
