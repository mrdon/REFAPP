package com.atlassian.refapp.backup;

import java.io.File;

public interface BackupManager
{
    void backup();

    void restore(File zip);
}
