package com.atlassian.maven.plugins.conf;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.atlassian.maven.plugins.refapp.AbstractWebappMojo;
import com.atlassian.maven.plugins.refapp.MavenContext;
import com.atlassian.maven.plugins.refapp.WebappArtifact;
import com.atlassian.maven.plugins.refapp.WebappHandler;

public class ConfluenceWebappHandler implements WebappHandler
{
    public String getId()
    {
        return "confluence";
    }

    public String getGroupId()
    {
        return "com.atlassian.confluence";
    }

    public String getArtifactId()
    {
        return "confluence-webapp";
    }

    public Map<String, String> getSystemProperties(final MavenProject project)
    {
        return Collections.singletonMap("confluence.home", getHomeDirectory(project).getPath());
    }

    public Collection<WebappArtifact> getSalArtifacts(final String salVersion)
    {
        return Arrays.asList(
                new WebappArtifact("com.atlassian.sal", "sal-api", salVersion),
                new WebappArtifact("com.atlassian.sal", "sal-confluence-plugin", salVersion));
    }

    public File getPluginsDirectory(final String webappDir)
    {
        // indicates plugins should be bundled
        return null;
    }

    public String getBundledPluginPath()
    {
        return "WEB-INF/classes/com/atlassian/confluence/setup/atlassian-bundled-plugins.zip";
    }

    public String getVersion()
    {
        return "RELEASE";
    }

    public File getHomeDirectory(final MavenProject project)
    {
        return new File(project.getBuild().getDirectory(), "confluence-home");
    }

    public void prepareWebapp(final File webappWar, final AbstractWebappMojo webappMojo) throws MojoExecutionException
    {
        final ConfluenceMavenGoals goals = new ConfluenceMavenGoals(new MavenContext(webappMojo.getProject(), webappMojo.getSession(), webappMojo.getPluginManager(), webappMojo.getLog()));
        final File outputDir = new File(webappMojo.getProject().getBuild().getDirectory());
        final File confHomeZip = goals.copyConfluenceHome(outputDir, webappMojo.getTestResourcesVersion());
        final File tmpDir = new File(webappMojo.getProject().getBuild().getDirectory(), "tmp-resources");
        tmpDir.mkdir();

        final File confHome = new File(outputDir, "confluence-home");
        try
        {
            webappMojo.unzip(confHomeZip, tmpDir.getPath());
            FileUtils.copyDirectory(new File(tmpDir.listFiles()[0], "confluence-home"),
                   confHome);

            final File cfgFile = new File(confHome, "confluence.cfg.xml");
            String config = FileUtils.readFileToString(cfgFile);
            config = config.replace("@project-dir@", webappMojo.getProject().getBuild().getDirectory());
            FileUtils.writeStringToFile(cfgFile, config);

            final File dbFile = new File(new File(confHome,"database"), "confluencedb.script");
            String db = FileUtils.readFileToString(dbFile);
            db = db.replace("<baseUrl>http://localhost:8080</baseUrl>", "<baseUrl>http://"+webappMojo.getServer()+":"+webappMojo.getHttpPort()+"/"+webappMojo.getContextPath().replaceAll("^/|/$", "")+"</baseUrl>");
            FileUtils.writeStringToFile(dbFile, db);
        }
        catch (final IOException ex)
        {
            throw new MojoExecutionException("Unable to prepare confluence webapp", ex);
        }
    }

}
