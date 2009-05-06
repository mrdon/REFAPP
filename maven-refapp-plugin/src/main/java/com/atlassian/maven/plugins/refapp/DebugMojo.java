package com.atlassian.maven.plugins.refapp;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Debug the webapp
 *
 * @requiresDependencyResolution debug
 * @goal debug
 * @execute phase="package"
 */
public class DebugMojo extends RunMojo
{
    /**
     * port for debugging
     *
     * @parameter expression="${jvm.debug.port}"
     */
    protected int jvmDebugPort = 5005;

    /**
     * Suspend when debugging
     *
     * @parameter expression="${jvm.debug.suspend}"
     */
    protected boolean jvmDebugSuspend = false;


    @Override
    public void execute() throws MojoExecutionException
    {
        if (jvmArgs == null)
        {
            jvmArgs = "-Xmx512m -XX:MaxPermSize=160m";
        }
        jvmArgs += " -Xdebug -Xrunjdwp:transport=dt_socket,address="+String.valueOf(jvmDebugPort)+",suspend="+(jvmDebugSuspend?"y":"n")+",server=y ";
        super.execute();
    }
}