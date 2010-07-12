package com.atlassian.refapp.backup;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.backup.Backup;
import com.atlassian.sal.api.backup.BackupRegistry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * <p>This is a simple and probably <em>naive</em> implementation of the {@link com.atlassian.sal.api.backup.BackupRegistry} as
 * defined by SAL.</p>
 * <p>Note that {@link Backup} objects/implementations are stored in-memory, in a {@link Set set} so
 * they must define propertly their {@link Object#hashCode() #hashCode} and {@link Object#equals(Object) #equals}
 * methods.</p>
 * <p>This also implements the actual {@link BackupManager backup} of the refapp.</p>
 */
public class RefAppBackupManager implements BackupManager
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BackupRegistry backupRegistry;
    private final ApplicationProperties applicationProperties;

    public RefAppBackupManager(BackupRegistry backupRegistry, ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
        this.backupRegistry = backupRegistry;
    }

    public void backup()
    {
        final File homeDir = applicationProperties.getHomeDirectory();
        if (homeDir == null)
        {
            throw new BackupException("Could not backup, home directory is not defined!");
        }

        final File backupFile = new File(homeDir, "backup_" + System.currentTimeMillis() + ".zip");
        log.info("Backing up to <" + backupFile.getAbsolutePath() + ">");

        OutputStream os = null;
        ArchiveOutputStream zip = null;
        try
        {
            os = new FileOutputStream(backupFile);
            zip = new ArchiveStreamFactory().createArchiveOutputStream("zip", os);

            for (Backup backup : backupRegistry.getRegistered())
            {
                try
                {
                    zip.putArchiveEntry(new ZipArchiveEntry(backup.getId()));
                    IOUtils.copy(backup.save(), zip);
                }
                catch (IOException e)
                {
                    log.error("Could not archive backup " + backup, e);
                }
                finally
                {
                    try
                    {
                        zip.closeArchiveEntry();
                    }
                    catch (IOException ignored)
                    {
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
            throw new BackupException(e.getMessage());
        }
        catch (ArchiveException e)
        {
            throw new BackupException(e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(zip);
            IOUtils.closeQuietly(os);
        }
    }

    public void restore(File backup)
    {
        final File tmpDir = createTmpDir();
        extractZip(backup, tmpDir);

        for (File pluginBackup : tmpDir.listFiles())
        {
            if (pluginBackup.isFile())
            {
                for (Backup b : backupRegistry.getRegistered())
                {
                    if (b.accept(pluginBackup.getName()))
                    {
                        FileInputStream is = null;
                        try
                        {
                            is = new FileInputStream(pluginBackup);
                            b.restore(is);
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                        finally
                        {
                            IOUtils.closeQuietly(is);
                        }
                    }
                }
            }
        }


    }

    private void extractZip(File backup, File tmpDir)
    {
        InputStream is = null;
        ArchiveInputStream in = null;
        try
        {
            is = new FileInputStream(backup);
            in = new ArchiveStreamFactory().createArchiveInputStream("zip", is);

            ZipArchiveEntry entry = (ZipArchiveEntry) in.getNextEntry();
            while (entry != null)
            {
                final File entryFile = new File(tmpDir, entry.getName());
                if (entry.isDirectory())
                {
                    entryFile.mkdirs();
                }
                else
                {
                    entryFile.getParentFile().mkdirs(); // make sure the dir structure is there
                    OutputStream out = null;
                    try
                    {
                        out = new FileOutputStream(entryFile);
                        IOUtils.copy(in, out);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                    finally
                    {
                        IOUtils.closeQuietly(out);
                    }

                    // proceed to the next entry
                    entry = (ZipArchiveEntry) in.getNextEntry();
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (ArchiveException
                e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(in);
        }
    }

    private File createTmpDir()
    {
        File tmpDir = null;
        try
        {
            tmpDir = File.createTempFile("backup", "refapp");
            tmpDir.delete();
            tmpDir.mkdirs();

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return tmpDir;
    }
}
