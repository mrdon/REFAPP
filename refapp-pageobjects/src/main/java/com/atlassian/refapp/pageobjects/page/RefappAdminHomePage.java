package com.atlassian.refapp.pageobjects.page;

import com.atlassian.pageobjects.page.AdminHomePage;
import com.atlassian.refapp.pageobjects.component.RefappHeader;

/**
 *
 */
public class RefappAdminHomePage extends RefappAbstractPage implements AdminHomePage<RefappHeader>
{

    public String getUrl()
    {
        return "/admin";
    }
}
