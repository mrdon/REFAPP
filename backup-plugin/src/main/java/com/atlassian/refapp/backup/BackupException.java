package com.atlassian.refapp.backup;

public class BackupException extends RuntimeException
{
    public BackupException(String message)
    {
        super(message);
    }
}