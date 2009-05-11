package com.atlassian.maven.plugins.refapp;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Run the webapp
 *
 * @requiresDependencyResolution run
 * @goal run
 * @execute phase="package"
 */
public class RunMojo
extends AbstractWebappMojo
{
    public void execute()
    throws MojoExecutionException
    {
        final MavenGoals goals = new MavenGoals(new MavenContext(project, session, pluginManager, getLog()), getWebappHandler());

        // Copy the webapp war to target
        final File webappWar = goals.copyWebappWar(targetDirectory, getVersion());

        final File combinedWebappWar = addArtifacts(goals, webappWar);

        getWebappHandler().prepareWebapp(combinedWebappWar, this);

        // Start the refapp
        final int actualHttpPort = goals.startWebapp(combinedWebappWar, containerId, server, httpPort, contextPath, jvmArgs);

        getLog().info(getWebappHandler().getId() + " started successfully and available at http://localhost:" + actualHttpPort + contextPath);
        getLog().info("Type any key to exit");
        try {
            System.in.read();
        } catch (final IOException e) {
            // ignore
        }
    }
}
