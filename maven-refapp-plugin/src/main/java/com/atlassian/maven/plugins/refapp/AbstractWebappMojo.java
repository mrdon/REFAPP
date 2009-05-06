package com.atlassian.maven.plugins.refapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
 * Base class for webapp mojos
 */
public abstract class AbstractWebappMojo extends AbstractMojo
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
     * @parameter expression="${http.port}" default-value="9400"
     */
    protected int httpPort;

    /**
     * Application context path
     *
     * @parameter expression="${context.path}"
     */
    protected String contextPath;

    /**
     * Application server
     *
     * @parameter expression="${server}" default-value="localhost"
     */
    protected String server;

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
     * A log4j properties file
     *
     * @parameter
     */
    protected File log4jProperties;

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
    private final List<WebappArtifact> pluginArtifacts = Collections.emptyList();

    /**
     * @parameter
     */
    private final List<WebappArtifact> libArtifacts = Collections.emptyList();

    /**
     * @parameter
     */
    private final List<WebappArtifact> bundledArtifacts = Collections.emptyList();

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

    public AbstractWebappMojo()
    {
        if (contextPath == null)
        {
            contextPath = "/" + getWebappHandler().getId();
        }
    }

    private List<WebappArtifact> getPluginsArtifacts()
    {
        final List<WebappArtifact> artifacts = new ArrayList<WebappArtifact>(pluginArtifacts);
        if (salVersion != null)
        {
            artifacts.addAll(getWebappHandler().getSalArtifacts(salVersion));
        }

        if (pdkVersion != null)
        {
            artifacts.add(new WebappArtifact("com.atlassian.pdkinstall", "pdkinstall-plugin", pdkVersion));
        }

        if (restVersion != null)
        {
            artifacts.add(new WebappArtifact("com.atlassian.plugins.rest", "atlassian-rest-module", restVersion));
        }

        if (rpmVersion != null)
        {
            artifacts.add(new WebappArtifact("com.atlassian.plugins.rest", "atlassian-rest-plugin-manager-plugin",
                    rpmVersion));
        }

        return artifacts;
    }

    protected File addArtifacts(final MavenGoals goals, final File webappWar) throws MojoExecutionException
    {
        try
        {
            final String webappDir = new File(project.getBuild().getDirectory(), "webapp").getAbsolutePath();
            unzip(webappWar, webappDir);

            final File pluginsDir = getWebappHandler().getPluginsDirectory(webappDir);
            final File bundledPluginsDir = new File(project.getBuild().getDirectory(), "bundled-plugins");
            bundledPluginsDir.mkdir();

            if (pluginsDir != null)
            {
                // add this plugin itself
                addThisPluginToDirectory(pluginsDir);
                // add plugins2 plugins
                addArtifactsToDirectory(goals, getPluginsArtifacts(), pluginsDir);
            }
            else
            {
                // add this plugin itself
                addThisPluginToDirectory(bundledPluginsDir);
                // add plugins2 plugins
                addArtifactsToDirectory(goals, getPluginsArtifacts(), bundledPluginsDir);
            }

            // add plugins1 plugins
            addArtifactsToDirectory(goals, libArtifacts, new File(webappDir, "WEB-INF/lib"));

            // add bundled plugins
            if (!bundledArtifacts.isEmpty())
            {
                addArtifactsToDirectory(goals, bundledArtifacts, bundledPluginsDir);
            }

            if (bundledPluginsDir.list().length > 0)
            {
                File bundledPluginsZip = new File(webappDir, getWebappHandler().getBundledPluginPath());
                if (bundledPluginsZip.exists()) {
                    unzip(bundledPluginsZip, bundledPluginsDir.getPath());
                }
                com.atlassian.core.util.FileUtils.createZipFile(bundledPluginsDir,bundledPluginsZip);
            }

            // add log4j.properties file if specified
            if (log4jProperties != null)
            {
                FileUtils.copyFile(log4jProperties, new File(webappDir, "WEB-INF/classes/log4j.properties"));
            }

            final File warFile = new File(webappWar.getParentFile(), getWebappHandler().getId() + ".war");
            com.atlassian.core.util.FileUtils.createZipFile(new File(webappDir), warFile);
            return warFile;

        }
        catch (final Exception e)
        {
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage());
        }
    }

    protected void unzip(final File zipFile, final String destDir) throws IOException
    {
        final ZipFile zip = new ZipFile(zipFile);
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
            }
            finally
            {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(fos);
            }
        }
    }

    private void addThisPluginToDirectory(final File pluginsDir) throws IOException
    {
        // add the plugin jar to the directory
        final File thisPlugin = getPluginFile();
        FileUtils.copyFile(thisPlugin, new File(pluginsDir, thisPlugin.getName()));
    }

    private File getPluginFile()
    {
        return new File(project.getBuild().getDirectory(), finalName + ".jar");
    }

    private void addArtifactsToDirectory(final MavenGoals goals, final List<WebappArtifact> artifacts,
                                         final File pluginsDir) throws MojoExecutionException
    {
        // first remove plugins from the webapp that we want to update
        if (pluginsDir.isDirectory() && pluginsDir.exists())
        {
            for (final Iterator<?> iterateFiles = FileUtils.iterateFiles(pluginsDir, null, false); iterateFiles.hasNext();)
            {
                final File file = (File) iterateFiles.next();
                for (final WebappArtifact webappArtifact : artifacts)
                {
                    if (!file.isDirectory() && file.getName()
                            .contains(webappArtifact.getArtifactId()))
                    {
                        file.delete();
                    }
                }
            }
        }
        // copy the all the plugins we want in the webapp
        if (!artifacts.isEmpty())
        {
            goals.copyPlugins(pluginsDir, artifacts);
        }
    }

    protected WebappHandler getWebappHandler()
    {
        return new RefappWebappHandler();
    }
}
