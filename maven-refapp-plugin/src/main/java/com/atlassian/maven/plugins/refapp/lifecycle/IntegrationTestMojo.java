package com.atlassian.maven.plugins.refapp.lifecycle;

import org.apache.maven.plugin.MojoExecutionException;
import com.atlassian.maven.plugins.refapp.AbstractRefappMojo;
import com.atlassian.maven.plugins.refapp.MavenGoals;

import java.io.File;

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
    private String functionalTestPattern = "it/**";

    /**
     * The directory containing generated test classes of the project being tested.
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testClassesDirectory;

    /**
     * The build directory
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File targetDirectory;

    /**
     * The jar name
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String finalName;


    public void execute()
            throws MojoExecutionException
    {
        if (!new File(testClassesDirectory, "it").exists())
        {
            getLog().info("No integration tests found");
            return;
        }
        MavenGoals goals = new MavenGoals(project, session, pluginManager, getLog());

        // Copy the refapp war to target
        File refappWar = goals.copyRefappWar(determineVersion());

        File combinedRefappWar = addPlugins(goals, refappWar);

        int actualHttpPort = goals.startRefapp(combinedRefappWar, containerId, httpPort, jvmArgs);

        String pluginJar = targetDirectory.getAbsolutePath() + "/" + finalName + ".jar";
        goals.runTests(containerId, functionalTestPattern, actualHttpPort, pluginJar);

        goals.stopRefapp(containerId);

    }
}