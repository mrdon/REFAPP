package com.atlassian.maven.plugins.refapp;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Run the refapp
 *
 * @requiresDependencyResolution run
 * @goal run
 * @execute phase="package"
 */
public class RunMojo
        extends AbstractRefappMojo
{
    public void execute()
            throws MojoExecutionException
    {
        final MavenGoals goals = new MavenGoals(project, session, pluginManager, getLog());

        // Copy the refapp war to target
        final File refappWar = goals.copyRefappWar(targetDirectory, determineVersion());

        final File combinedRefappWar = addArtifacts(goals, refappWar);

        // Start the refapp
        goals.startRefapp(combinedRefappWar, containerId, httpPort, jvmArgs);

        getLog().info("Refapp started successfully and available at http://localhost:"+httpPort+"/refapp");
        getLog().info("Type any key to exit");
        try {
            System.in.read();
        } catch (final IOException e) {
            // ignore
        }
    }
}
