package com.atlassian.refapp.auth.internal;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class RedirectHelper
{
    public static void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String location = request.getContextPath();

        String redirParam = request.getParameter("redir");
        if (!StringUtils.isEmpty(redirParam))
        {
            if (redirParam.startsWith("http:") || redirParam.startsWith("https:"))
            {
                location = redirParam;
            }
            else 
            {
                if (!redirParam.startsWith("/"))
                {
                    location += "/";
                }
                location += redirParam;
            }
        }

        response.sendRedirect(location);
    }

}
