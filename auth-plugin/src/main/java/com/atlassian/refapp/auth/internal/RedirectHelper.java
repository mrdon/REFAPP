package com.atlassian.refapp.auth.internal;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class RedirectHelper
{
    public static void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String redir = request.getContextPath();

        String redirPath = request.getParameter("redir");
        if (!StringUtils.isEmpty(redirPath))
        {
            if (redirPath.startsWith("http:") || redirPath.startsWith("https:"))
            {
                redir = redirPath;
            }
            else 
            {
                if (!redirPath.startsWith("/"))
                {
                    redir += "/";
                }
                redir += redirPath;
            }
        }

        response.sendRedirect(redir);
    }

}
