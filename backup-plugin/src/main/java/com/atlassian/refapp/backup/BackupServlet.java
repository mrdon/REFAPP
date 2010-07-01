package com.atlassian.refapp.backup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackupServlet extends HttpServlet
{
    private final BackupManager backupManager;

    public BackupServlet(BackupManager backupManager)
    {
        this.backupManager = checkNotNull(backupManager);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            backupManager.backup();
            res.getWriter().write("<h1>Backup successful</h1>");
        }
        catch (BackupException e)
        {
            res.getWriter().write("<h1>Backup failed!</h1>");
            res.getWriter().write("<p>" + e.getMessage() + "</p>");
        }

        res.getWriter().close();
    }

    private static <T> T checkNotNull(T t)
    {
        if (t == null)
            throw new IllegalArgumentException();
        return t;
    }
}
