package com.atlassian.maven.plugins.refapp.lifecycle;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

import com.atlassian.maven.plugins.refapp.AbstractRefappMojo;
import com.atlassian.maven.plugins.refapp.MavenGoals;

/**
 * Run the integration tests against the refapp
 *
 * @requiresDependencyResolution integration-test
 * @goal integration-test
 * @phase integration-test
 */
public class IntegrationTestMojo
        extends AbstractRefappMojo {

    /**
     * Pattern for to use to find integration tests
     *
     * @parameter expression="${functionalTestPattern}"
     */
    private final String functionalTestPattern = "it/**";

    /**
     * The directory containing generated test classes of the project being tested.
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testClassesDirectory;


    public void execute()
            throws MojoExecutionException
    {
        if (!new File(testClassesDirectory, "it").exists())
        {
            getLog().info("No integration tests found");
            return;
        }
        final MavenGoals goals = new MavenGoals(project, session, pluginManager, getLog());

        // Copy the refapp war to target
        final File refappWar = goals.copyRefappWar(determineVersion());

        final File combinedRefappWar = addPlugins(goals, refappWar);

        final int actualHttpPort = goals.startRefapp(combinedRefappWar, containerId, httpPort, jvmArgs);

        final String pluginJar = targetDirectory.getAbsolutePath() + "/" + finalName + ".jar";
        goals.runTests(containerId, functionalTestPattern, actualHttpPort, pluginJar);

        goals.stopRefapp(containerId);

    }
}