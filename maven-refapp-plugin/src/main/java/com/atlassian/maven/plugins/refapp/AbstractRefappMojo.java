package com.atlassian.maven.plugins.refapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;

/**
 * Base class for refapp mojos
 */
public abstract class AbstractRefappMojo extends AbstractMojo
{
    /**
     * Container to run in
     *
     * @parameter expression="${container}"
     */
    protected String containerId = "tomcat6x";
    /**
     * HTTP port for the servlet containers
     *
     * @parameter expression="${http.port}"
     */
    protected int httpPort = 9400;

    /**
     * The build directory
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File targetDirectory;

    /**
     * The jar name
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    protected String finalName;

    /**
     * JVM arguments to pass to cargo
     *
     * @parameter expression="${jvmargs}"
     */
    protected String jvmArgs = null;
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
     * @parameter
     */
    private final List<RefappArtifact> pluginArtifacts = Collections.emptyList();

    /**
     * @parameter
     */
    private final List<RefappArtifact> libArtifacts = Collections.emptyList();

    /**
     * @parameter
     */
    private final List<RefappArtifact> bundledArtifacts = Collections.emptyList();

    /**
     * SAL version
     *
     * @parameter expression="${sal.version}
     */
    private String salVersion;

    /**
     * Atlassian Plugin Development Kit (PDK) version
     *
     * @parameter expression="${pdk.version}
     */
    private String pdkVersion;

    /**
     * Atlassian REST module version
     *
     * @parameter expression="${rest.version}
     */
    private String restVersion;

    /**
     * Atlassian REST Plugin Manager version
     *
     * @parameter expression="${rpm.version}
     */
    private String rpmVersion;

    private List<RefappArtifact> getPluginsArtifacts()
    {
        final List<RefappArtifact> artifacts = new ArrayList<RefappArtifact>(pluginArtifacts);
        if (salVersion != null)
        {
            artifacts.addAll(Arrays.asList(new RefappArtifact("com.atlassian.sal", "sal-api", salVersion),
                    new RefappArtifact("com.atlassian.sal", "sal-refimpl-appproperties-plugin", salVersion),
                    new RefappArtifact("com.atlassian.sal", "sal-refimpl-component-plugin", salVersion),
                    new RefappArtifact("com.atlassian.sal", "sal-refimpl-executor-plugin", salVersion),
                    new RefappArtifact("com.atlassian.sal", "sal-refimpl-lifecycle-plugin", salVersion),
                    new RefappArtifact("com.atlassian.sal", "sal-refimpl-message-plugin", salVersion),
                    new RefappArtifact("com.atlassian.sal", "sal-refimpl-net-plugin", salVersion), new RefappArtifact(
                            "com.atlassian.sal", "sal-refimpl-pluginsettings-plugin", salVersion), new RefappArtifact(
                            "com.atlassian.sal", "sal-refimpl-project-plugin", salVersion), new RefappArtifact(
                            "com.atlassian.sal", "sal-refimpl-search-plugin", salVersion), new RefappArtifact(
                            "com.atlassian.sal", "sal-refimpl-transaction-plugin", salVersion), new RefappArtifact(
                            "com.atlassian.sal", "sal-refimpl-upgrade-plugin", salVersion), new RefappArtifact(
                            "com.atlassian.sal", "sal-refimpl-user-plugin", salVersion)));
        }

        if (pdkVersion != null)
        {
            artifacts.add(new RefappArtifact("com.atlassian.pdkinstall", "pdkinstall-plugin", pdkVersion));
        }

        if (restVersion != null)
        {
            artifacts.add(new RefappArtifact("com.atlassian.plugins.rest", "atlassian-rest-module", restVersion));
        }

        if (rpmVersion != null)
        {
            artifacts.add(new RefappArtifact("com.atlassian.plugins.rest", "atlassian-rest-plugin-manager-plugin",
                    rpmVersion));
        }

        return artifacts;
    }

    protected File addArtifacts(final MavenGoals goals, final File refappWar) throws MojoExecutionException
    {
        try
        {
            final String refappDir = new File(project.getBuild().getDirectory(), "refapp").getAbsolutePath();
            unzip(refappWar, refappDir);

            final File refappPluginsDir = new File(refappDir, "WEB-INF/plugins");
            // add this plugin itself
            addThisPluginToDirectory(refappPluginsDir);
            // add plugins2 plugins
            addArtifactsToDirectory(goals, getPluginsArtifacts(), refappPluginsDir);
            // add plugins1 plugins
            addArtifactsToDirectory(goals, libArtifacts, new File(refappDir, "WEB-INF/lib"));
            // add bundled plugins
            final File bundledPluginsDir = new File(project.getBuild().getDirectory(), "refapp-bundled-plugins");
            addArtifactsToDirectory(goals, bundledArtifacts, bundledPluginsDir);
            com.atlassian.core.util.FileUtils.createZipFile(bundledPluginsDir, new File(refappDir,
                    "WEB-INF/classes/atlassian-bundled-plugins.zip"));

            final File warFile = new File(refappWar.getParentFile(), "refapp.war");
            com.atlassian.core.util.FileUtils.createZipFile(new File(refappDir), warFile);
            return warFile;

        } catch (final Exception e)
        {
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage());
        }
        // File war = refappWar;
        // return addBundledPlugins(goals, war);
    }

    private void unzip(final File refappWar, final String destDir) throws ZipException, IOException
    {
        final ZipFile zip = new ZipFile(refappWar);
        final Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements())
        {
            final ZipEntry zipEntry = entries.nextElement();
            final File file = new File(destDir + "/" + zipEntry.getName());
            if (zipEntry.isDirectory())
            {
                file.mkdirs();
                continue;
            }
            InputStream is = null;
            OutputStream fos = null;
            try
            {
                is = zip.getInputStream(zipEntry);
                fos = new FileOutputStream(file);
                IOUtils.copy(is, fos);
            } finally
            {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(fos);
            }
        }
    }

    private void addThisPluginToDirectory(final File refappPluginsDir) throws IOException
    {
        // add the plugin jar to the directory
        final File thisPlugin = new File(project.getBuild().getDirectory(), finalName + ".jar");
        FileUtils.copyFile(thisPlugin, new File(refappPluginsDir, thisPlugin.getName()));
    }

    private void addArtifactsToDirectory(final MavenGoals goals, final List<RefappArtifact> artifacts,
            final File refappPluginsDir) throws MojoExecutionException
    {
        // first remove plugins from the refapp that we want to update
        if (refappPluginsDir.isDirectory() && refappPluginsDir.exists())
        {
            for (final Iterator<?> iterateFiles = FileUtils.iterateFiles(refappPluginsDir, null, false); iterateFiles.hasNext();)
            {
                final File file = (File) iterateFiles.next();
                for (final RefappArtifact refappArtifact : artifacts)
                {
                    if (!file.isDirectory() && file.getName().contains(refappArtifact.getArtifactId()))
                    {
                        file.delete();
                    }
                }
            }
        }
        // copy the all the plugins we want in the refapp
        if (!artifacts.isEmpty())
        {
            goals.copyPlugins(refappPluginsDir, artifacts);
        }
    }

    protected String determineVersion()
    {
        InputStream in = null;
        final Properties props = new Properties();
        try
        {
            in = getClass().getClassLoader().getResourceAsStream(
                    "META-INF/maven/com.atlassian.maven.plugins/maven-refapp-plugin/pom.properties");
            if (in != null)
            {
                props.load(in);
                return props.getProperty("version");
            }
        } catch (final IOException e)
        {
            e.printStackTrace();
            return null;
        } finally
        {
            IOUtils.closeQuietly(in);
        }
        return null;
    }
}
