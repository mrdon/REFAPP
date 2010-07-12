package com.atlassian.refapp.backup;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class RestoreServlet extends HttpServlet
{
    private final BackupManager backupManager;

    public RestoreServlet(BackupManager backupManager)
    {
        this.backupManager = checkNotNull(backupManager);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        final PrintWriter w = res.getWriter();
        w.write("<h1>Restore</h1>");
        w.write("<form method=\"post\" enctype=\"multipart/form-data\">");
        w.write("<label for=\"zip\">Zip:</label>");
        w.write("<input type=\"file\" id=\"zip\" name=\"zip\" value=\"\"/>");
        w.write("<br>");
        w.write("<input type=\"submit\" name=\"submit\" value=\"Restore\"/>");
        w.write("</form>");
        w.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        final FileItemFactory factory = new DiskFileItemFactory();
        final ServletFileUpload upload = new ServletFileUpload(factory);
        try
        {
            FileItem item = (FileItem) upload.parseRequest(req).get(0);
            final File tempFile = File.createTempFile("upload", "backup");
            item.write(tempFile);
            backupManager.restore(tempFile);
            resp.getWriter().write("<h1>Restore successful</h1>");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static <T> T checkNotNull(T t)
    {
        if (t == null)
            throw new IllegalArgumentException();
        return t;
    }
}
