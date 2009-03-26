package com.atlassian.maven.plugins.refapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;

/**
 * Base class for refapp mojos
 */
public abstract class AbstractRefappMojo extends AbstractMojo {
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
     * @parameter expression="${project.artifact}"
     * @required
     * @readonly
     */
    private Artifact artifact;

    /**
     * @parameter
     */
    private final List<PluginArtifact> pluginArtifacts = Collections.emptyList();

    /**
     * SAL version
     *
     * @parameter expression="${sal.version}
     */
    private String salVersion;

    /**
     * Atlassian Plugin Development Kit (PDK) version
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

    private List<PluginArtifact> getPluginArtifacts()
    {
        final List<PluginArtifact> artifacts = new ArrayList<PluginArtifact>(pluginArtifacts);
        if (salVersion != null)
        {
            artifacts.addAll(Arrays.asList(
                    new PluginArtifact("com.atlassian.sal", "sal-api", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-appproperties-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-component-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-executor-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-lifecycle-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-message-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-net-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-pluginsettings-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-project-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-search-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-transaction-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-upgrade-plugin", salVersion),
                    new PluginArtifact("com.atlassian.sal", "sal-refimpl-user-plugin", salVersion)));
        }

        if (pdkVersion != null)
        {
            artifacts.add(new PluginArtifact("com.atlassian.pdkinstall", "pdkinstall-plugin", pdkVersion));
        }

        if (restVersion != null)
        {
            artifacts.add(new PluginArtifact("com.atlassian.plugins.rest", "atlassian-rest-module", restVersion));
        }

        if (rpmVersion != null)
        {
            artifacts.add(new PluginArtifact("com.atlassian.plugins.rest", "atlassian-rest-plugin-manager-plugin", rpmVersion));
        }

        return artifacts;
    }


    protected File addPlugins(final MavenGoals goals, final File refappWar) throws MojoExecutionException {
        final File pluginsDir = new File(new File("target"), "plugins");
        if (!pluginsDir.exists())
        {
            pluginsDir.mkdir();
        }

        final List<PluginArtifact> artifacts = getPluginArtifacts();
        if (!artifacts.isEmpty())
        {
            goals.copyPlugins(pluginsDir, artifacts);
        }

        final String pluginJar = targetDirectory.getAbsolutePath() + "/" + finalName + ".jar";

        final File combinedRefappWar = new File(refappWar.getParentFile(), "refapp.war");

        ZipInputStream zin = null;
        ZipOutputStream zout = null;

        try
        {
            FileUtils.copyFile(new File(pluginJar), new File(pluginsDir, finalName + ".jar"));
            zin = new ZipInputStream(new FileInputStream(refappWar));
            zout = new ZipOutputStream(new FileOutputStream(combinedRefappWar));

            ZipEntry source;
            outer:
            while ((source = zin.getNextEntry()) != null)
            {
                for (final PluginArtifact plugin : artifacts)
                {
                    if (source.getName().contains(plugin.getArtifactId()))
                    {
                        continue outer;
                    }
                }
                final ZipEntry dest = new ZipEntry(source);
                zout.putNextEntry(dest);
                IOUtils.copy(zin, zout);
            }

            for (final File pluginFile : pluginsDir.listFiles())
            {
                addFileToZip(zout, pluginFile);
            }

            addFileToZip(zout, artifact.getFile());
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        } finally
        {
            IOUtils.closeQuietly(zin);
            IOUtils.closeQuietly(zout);
        }
        return combinedRefappWar;
    }

    private void addFileToZip(final ZipOutputStream zout, final File pluginFile) throws IOException {
        if (pluginFile == null)
        {
            return;
        }
        final ZipEntry dest = new ZipEntry("WEB-INF/plugins/"+pluginFile.getName());
        dest.setTime(pluginFile.lastModified());
        zout.putNextEntry(dest);

        InputStream in = null;
        try
                {
                    in = new FileInputStream(pluginFile);
            IOUtils.copy(in, zout);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }

    protected String determineVersion()
    {
        InputStream in = null;
        final Properties props = new Properties();
        try
        {
            in = getClass().getClassLoader().getResourceAsStream("META-INF/maven/com.atlassian.maven.plugins/maven-refapp-plugin/pom.properties");
            if (in != null)
            {
                props.load(in);
                return props.getProperty("version");
            }
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        return null;
    }
}
