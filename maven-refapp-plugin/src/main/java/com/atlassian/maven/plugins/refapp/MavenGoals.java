package com.atlassian.maven.plugins.refapp;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import com.atlassian.maven.plugins.refapp.util.VersionUtils;

/**
 * Executes specific maven goals
 */
public class MavenGoals {
    private final MavenProject project;
    private final MavenSession session;
    private final PluginManager pluginManager;
    private final Log log;
    private final Map<String, String> pluginArtifactIdToVersionMap;
    private final WebappHandler webappHandler;

    private final Map<String, Container> idToContainerMap = new HashMap<String, Container>()
    {{
        put("tomcat5x", new Container("tomcat5x", "https://m2proxy.atlassian.com/repository/public/org/apache/tomcat/apache-tomcat/5.5.25/apache-tomcat-5.5.25.zip"));
        put("tomcat6x", new Container("tomcat6x", "http://apache.mirror.aussiehq.net.au/tomcat/tomcat-6/v6.0.18/bin/apache-tomcat-6.0.18.zip"));
        put("resin3x", new Container("resin3x", "http://www.caucho.com/download/resin-3.0.26.zip"));
        put("jboss42x", new Container("jboss42x", "http://internode.dl.sourceforge.net/sourceforge/jboss/jboss-4.2.3.GA.zip"));
        put("jetty6x", new Container("jetty6x"));

    }};

    private final Map<String,String> defaultArtifactIdToVersionMap = new HashMap<String,String>()
    {{
        put("maven-cli-plugin", "0.6.2");
        put("cargo-maven2-plugin", "1.0-beta-2-db2");
        put("atlassian-pdk", "2.1.6");
        put("maven-archetype-plugin", "2.0-alpha-4");

    }};

    public MavenGoals(MavenContext ctx, WebappHandler webappHandler) {
        this(ctx, webappHandler, Collections.<String, String>emptyMap());
    }
    public MavenGoals(MavenContext ctx, WebappHandler webappHandler, final Map<String,String> pluginToVersionMap) {
        this.project = ctx.getProject();
        this.session = ctx.getSession();
        this.pluginManager = ctx.getPluginManager();
        this.log = ctx.getLog();
        this.webappHandler = webappHandler;

        final Map<String,String> map = new HashMap<String, String>(defaultArtifactIdToVersionMap);
        map.putAll(pluginToVersionMap);
        this.pluginArtifactIdToVersionMap = Collections.unmodifiableMap(map);

    }

    public void startCli(final int port) throws MojoExecutionException {

        final List<Element> configs = new ArrayList<Element>();
        configs.add(element(name("commands"),
                element(name("pi"), "resources com.atlassian.maven.plugins:maven-"+webappHandler.getId()+"-plugin:filter-plugin-descriptor "
                        + "compile com.atlassian.maven.plugins:maven-"+webappHandler.getId()
                        +"-plugin:copy-bundled-dependencies jar com.atlassian.maven.plugins:maven-"+webappHandler.getId()+"-plugin:install"),
                element(name("pu"), "com.atlassian.maven.plugins:maven-"+webappHandler.getId()+"-plugin:uninstall")));
        if (port > 0)
        {
            configs.add(element(name("port"), String.valueOf(port)));
        }
        executeMojo(
                plugin(
                        groupId("org.twdata.maven"),
                        artifactId("maven-cli-plugin"),
                        version(pluginArtifactIdToVersionMap.get("maven-cli-plugin"))
                ),
                goal("execute"),
                configuration(configs.toArray(new Element[0])),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public void createPlugin() throws MojoExecutionException {

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-archetype-plugin"),
                        version(defaultArtifactIdToVersionMap.get("maven-archetype-plugin"))
                ),
                goal("generate"),
                configuration(
                        element(name("archetypeGroupId"), "com.atlassian.maven.archetypes"),
                        element(name("archetypeArtifactId"), webappHandler.getId() + "-plugin-archetype"),
                        element(name("archetypeVersion"), VersionUtils.getVersion())
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public void copyBundledDependencies() throws MojoExecutionException {
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin")
                ),
                goal("copy-dependencies"),
                configuration(
                        element(name("includeScope"), "runtime"),
                        element(name("excludeScope"), "provided"),
                        element(name("excludeScope"), "test"),
                        element(name("type"), "jar"),
                        element(name("outputDirectory"), "${project.build.outputDirectory}/META-INF/lib")
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public void filterPluginDescriptor() throws MojoExecutionException {
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-resources-plugin")
                ),
                goal("copy-resources"),
                configuration(
                        element(name("resources"),
                                element(name("resource"),
                                        element(name("directory"), "src/main/resources"),
                                        element(name("filtering"), "true"),
                                        element(name("includes"),
                                                element(name("include"), "atlassian-plugin.xml"))
                                )
                        ),
                        element(name("outputDirectory"), "${project.build.outputDirectory}")
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public void runUnitTests() throws MojoExecutionException {
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-surefire-plugin")
                ),
                goal("test"),
                configuration(
                        element(name("excludes"),
                                element(name("exclude"), "it/**"),
                                element(name("exclude"), "**/*$*"))
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public File copyWebappWar(final File targetDirectory, final String webappVersion) throws MojoExecutionException {
        final File webappWarFile = new File(targetDirectory, webappHandler.getId()+"-original.war");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin")
                ),
                goal("copy"),
                configuration(
                        element(name("artifactItems"),
                                element(name("artifactItem"),
                                        element(name("groupId"), webappHandler.getArtifact().getGroupId()),
                                        element(name("artifactId"), webappHandler.getArtifact().getArtifactId()),
                                        element(name("type"), "war"),
                                        element(name("version"), webappVersion),
                                        element(name("destFileName"), webappWarFile.getName()))),
                                        element(name("outputDirectory"), targetDirectory.getPath())
                ),
                executionEnvironment(project, session, pluginManager)
        );
        return webappWarFile;
    }

    public void copyPlugins(final File pluginsDir, final List<WebappArtifact> pluginArtifacts) throws MojoExecutionException {
        final Element[] items = new Element[pluginArtifacts.size()];
        for (int x=0; x<pluginArtifacts.size(); x++)
        {
            final WebappArtifact artifact = pluginArtifacts.get(x);
            items[x] = element(name("artifactItem"),
                    element(name("groupId"), artifact.getGroupId()),
                    element(name("artifactId"), artifact.getArtifactId()),
                    element(name("version"), artifact.getVersion()));
        }
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin")
                ),
                goal("copy"),
                configuration(
                        element(name("artifactItems"), items),
                        element(name("outputDirectory"), pluginsDir.getPath())
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public int startWebapp(WebappContext webappContext) throws MojoExecutionException {
        final int rmiPort = pickFreePort(0);
        final int actualHttpPort = pickFreePort(webappContext.getHttpPort());
        final Container container = findContainer(webappContext.getContainerId());
        List<Element> sysProps = new ArrayList<Element>();
        if (webappContext.getJvmArgs() == null)
        {
            webappContext.setJvmArgs("-Xmx512m -XX:MaxPermSize=160m");
        }
        for (Map.Entry<String,String> entry : webappHandler.getSystemProperties(project).entrySet())
        {
            webappContext.setJvmArgs(webappContext.getJvmArgs() + " -D" + entry.getKey() + "=" + entry.getValue());
            sysProps.add(element(name(entry.getKey()), entry.getValue()));
        }
        log.info("Starting "+webappHandler.getId()+" on the " + container.getId() + " container on ports "
                + actualHttpPort + " (http) and " + rmiPort + " (rmi)");

        final String baseUrl = getBaseUrl(webappContext.getServer(), actualHttpPort, webappContext.getContextPath());
        sysProps.add(element(name("baseurl"), baseUrl));

        List<Element> deps = new ArrayList<Element>();
        for (WebappArtifact dep : webappHandler.getExtraContainerDependencies())

        {
            deps.add(element(name("dependency"),
                        element(name("location"), webappContext.getArtifactRetriever().resolve(dep))
                    ));
        }

        List<Element> props = new ArrayList<Element>();
        for (Map.Entry<String,String> entry : webappHandler.getSystemProperties(project).entrySet())
        {
            props.add(element(name(entry.getKey()), entry.getValue()));
        }
        props.add(element(name("cargo.servlet.port"), String.valueOf(actualHttpPort)));
        props.add(element(name("cargo.rmi.port"), String.valueOf(rmiPort)));
        props.add(element(name("cargo.jvmargs"), webappContext.getJvmArgs()));

        executeMojo(
                plugin(
                        groupId("org.twdata.maven"),
                        artifactId("cargo-maven2-plugin"),
                        version(pluginArtifactIdToVersionMap.get("cargo-maven2-plugin"))
                ),
                goal("start"),
                configuration(
                        element(name("wait"), "false"),
                        element(name("container"),
                                element(name("containerId"), container.getId()),
                                element(name("type"), container.getType()),
                                element(name("zipUrlInstaller"),
                                        element(name("url"), container.getUrl())
                                ),
                                element(name("systemProperties"), sysProps.toArray(new Element[sysProps.size()])),
                                element(name("dependencies"), deps.toArray(new Element[deps.size()]))
                        ),
                        element(name("configuration"),
                                element(name("home"), "${project.build.directory}/" + webappHandler.getId() + "/" + container.getId()),
                                element(name("properties"), props.toArray(new Element[props.size()])),
                                element(name("deployables"),
                                        element(name("deployable"),
                                                element(name("groupId"), webappHandler.getArtifact().getGroupId()),
                                                element(name("artifactId"), webappHandler.getArtifact().getArtifactId()),
                                                element(name("type"), "war"),
                                                element(name("location"), webappContext.getWebappWar().getPath())
                                        )
                                )
                        )
                ),
                executionEnvironment(project, session, pluginManager)
        );
        return actualHttpPort;
    }

    private String getBaseUrl(final String server, final int actualHttpPort, final String contextPath)
    {
        return "http://"+server+":"+actualHttpPort+contextPath;
    }

    public void runTests(final String containerId, final String functionalTestPattern, final int httpPort, final String contexPath, final String pluginJar) throws MojoExecutionException {
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-surefire-plugin")
                ),
                goal("test"),
                configuration(
                        element(name("includes"),
                                element(name("include"), functionalTestPattern)
                        ),
                        element(name("excludes"),
                                element(name("exclude"), "**/*$*")
                        ),
                        element(name("systemProperties"),
                                element(name("property"),
                                        element(name("name"), "http.port"),
                                        element(name("value"), String.valueOf(httpPort))
                                ),
                                element(name("property"),
                                        element(name("name"), "context.path"),
                                        element(name("value"), contexPath)
                                ),
                                element(name("property"),
                                        element(name("name"), "plugin.jar"),
                                        element(name("value"), pluginJar)
                                )
                        ),
                        element(name("reportsDirectory"), "${project.build.directory}/" + webappHandler.getId() + "/" + containerId + "/surefire-reports")
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    private Container findContainer(final String containerId)
    {
        final Container container = idToContainerMap.get(containerId);
        if (container == null)
        {
            throw new IllegalArgumentException("Container " + containerId + " not supported");
        }
        return container;
    }

    private int pickFreePort(final int requestedPort)
    {
        if (requestedPort > 0)
        {
            return requestedPort;
        }
        ServerSocket socket = null;
        try
        {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Error opening socket", e);
        }
        finally
        {
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (final IOException e)
                {
                    throw new RuntimeException("Error closing socket", e);
                }
            }
        }
    }

    public void stopWebapp(final String containerId) throws MojoExecutionException {
        final Container container = findContainer(containerId);
        executeMojo(
                plugin(
                        groupId("org.twdata.maven"),
                        artifactId("cargo-maven2-plugin"),
                        version(pluginArtifactIdToVersionMap.get("cargo-maven2-plugin"))
                ),
                goal("stop"),
                configuration(
                        element(name("container"),
                                element(name("containerId"), container.getId()),
                                element(name("type"), container.getType())
                        ),
                        element(name("configuration"),
                                element(name("home"), "${project.build.directory}/" + webappHandler.getId() + "/" + container.getId())
                        )
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public void installPlugin(final String pluginKey, final String server, final int port, final String contextPath) throws MojoExecutionException {
        final String baseUrl = getBaseUrl(server, port, contextPath);
        executeMojo(
                plugin(
                        groupId("com.atlassian.maven.plugins"),
                        artifactId("atlassian-pdk"),
                        version(pluginArtifactIdToVersionMap.get("atlassian-pdk"))
                ),
                goal("install"),
                configuration(
                        element(name("username"), "admin"),
                        element(name("password"), "admin"),
                        element(name("serverUrl"), baseUrl),
                        element(name("pluginKey"), pluginKey)
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public void uninstallPlugin(final String pluginKey, final int port) throws MojoExecutionException {
        executeMojo(
                plugin(
                        groupId("com.atlassian.maven.plugins"),
                        artifactId("atlassian-pdk"),
                        version(pluginArtifactIdToVersionMap.get("atlassian-pdk"))
                ),
                goal("uninstall"),
                configuration(
                        element(name("username"), "admin"),
                        element(name("password"), "admin"),
                        element(name("serverUrl"), "http://localhost:"+port+"/"+webappHandler.getId()),
                        element(name("pluginKey"), pluginKey)
                ),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public void installIdeaPlugin() throws MojoExecutionException {
        executeMojo(
                plugin(
                        groupId("org.twdata.maven"),
                        artifactId("maven-cli-plugin"),
                        version(pluginArtifactIdToVersionMap.get("maven-cli-plugin"))
                ),
                goal("idea"),
                configuration(),
                executionEnvironment(project, session, pluginManager)
        );
    }

    public File copyHome(File targetDirectory, String testResourcesVersion) throws MojoExecutionException
    {
        final File testResourcesZip = new File(targetDirectory, "test-resources.zip");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin")
                ),
                goal("copy"),
                configuration(
                        element(name("artifactItems"),
                                element(name("artifactItem"),
                                        element(name("groupId"), webappHandler.getTestResourcesArtifact().getGroupId()),
                                        element(name("artifactId"), webappHandler.getTestResourcesArtifact().getArtifactId()),
                                        element(name("type"), "zip"),
                                        element(name("version"), testResourcesVersion),
                                        element(name("destFileName"), testResourcesZip.getName()))),
                                        element(name("outputDirectory"), testResourcesZip.getParent())
                ),
                executionEnvironment(project, session, pluginManager)
        );
        return testResourcesZip;
    }

    private static class Container
    {
        private final String id;
        private final String type;
        private final String url;

        public Container(final String id, final String url)
        {
            this.id = id;
            this.type = "installed";
            this.url = url;
        }

        public Container(final String id)
        {
            this.id = id;
            this.type = "embedded";
            this.url = null;
        }

        public String getId()
        {
            return id;
        }

        public String getType()
        {
            return type;
        }

        public String getUrl()
        {
            return url;
        }
    }
}
