package com.atlassian;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.python.util.PythonInterpreter;
import org.twdata.enchanter.StreamConnection;
import org.twdata.enchanter.impl.DefaultStreamConnection;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Mojo which executes the enchanter.
 *
 * @goal enchanter
 */
public class EnchanterMojo extends AbstractMojo
{
    private static final String PYTHON_EXTENSION = ".py";

   /**
    * Script file to execute.
    * The location is relative to the src root (where the pom.xml file located).
    *
    * @parameter
    */
    private String scriptFile;

    /**
     * The config file which will be used to resolve variables in scriptFile.
     * The location is relative to the src root (where the pom.xml file located).
     *
     * @parameter
     */
    private String configFile;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        // the scriptFile parameter must be supplied.
        if (scriptFile == null || scriptFile.length() == 0)
        {
            throw new MojoExecutionException("scriptFile parameter is needed for executing the EnchanterMojo");
        }

        // get the absolute path.
        final String absoluteScriptPath = resolveAbsolutePath(scriptFile);

        // ensure that the script file exists.
        final File scriptFp = new File(absoluteScriptPath);
        if (!scriptFp.exists())
        {
            throw new MojoExecutionException(String.format("the specified scriptFile %s doesn't exist.", absoluteScriptPath));
        }

        // ensure that we have the right permission to read it.
        if (!scriptFp.canRead()) {
            throw new MojoExecutionException(String.format("cannot read the scriptFile %s due to permissions", absoluteScriptPath));
        }

        getLog().info("====================Executing enchanter====================");

        // delegate to the right executor.
        if (scriptFile.endsWith(PYTHON_EXTENSION))
        {
            // the map for keeping all variables in the config file.
            Map<String, String> variables;

            if (configFile == null || configFile.length() == 0)
            {
                getLog().info("No config file supplied. No variable resolution occurred in the scriptFile");
                variables  = Collections.emptyMap();
            }
            else
            {
                // load config file
                String absoluteConfigPath = resolveAbsolutePath(configFile);
                File configFp = new File(absoluteConfigPath);

                Properties props = new Properties();
                try
                {
                    props.load(new FileInputStream(configFp));
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException(String.format("the specified configFile %s cannot be opened.", absoluteConfigPath));
                }

                // load all the properties to the variables map.
                variables = new HashMap<String, String>((Map)props);
            }

            ParamResolver paramResolver = new ParamResolver();
            String originalScript = null;
            try 
            {
                originalScript = readFileToString(scriptFp);
            }
            catch (IOException e)
            {
                throw new MojoExecutionException(String.format("the specified script %s cannot be read.", absoluteScriptPath));
            }

            String resolvedScript = paramResolver.resolve(originalScript, variables);
            executeInPython(resolvedScript);
        }
        else
        {
            throw new MojoExecutionException("the mojo currently only supports Python (*.py)!!!");
        }

        getLog().info("====================Finished the execution====================");
    }

    /**
     * Resolve the given scriptFile to the absolute path.
     */
    private String resolveAbsolutePath(String filename)
    {
        return filename;
    }

    /**
     * Read the input file to string.
     * This is not a good approach in general but our script files are expected
     * to be very small thus this would not affect the performance.
     *
     * @param fp the file.
     *
     * @return string content of the file.
     * @throws java.io.IOException error during reading the file.
     */
    private String readFileToString(File fp) throws IOException {

        final StringBuilder sb = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new FileReader(fp));

        String line;
        while((line=reader.readLine()) != null)
        {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }

        // close the file and return
        reader.close();
        return sb.toString();
    }

    /**
     * Execute the python script.
     *
     * @param script the script string (not file location).
     */
    private void executeInPython(String script)
    {
        // The script interpreter
        PythonInterpreter interp = new PythonInterpreter();
        StreamConnection conn = new DefaultStreamConnection();

        // pass variables to the python interpreter.
        interp.set("ssh", conn);
        interp.set("conn", conn);
        interp.exec(script);
    }
}
