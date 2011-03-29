package com.atlassian.refapp.pageobjects.page;

import com.atlassian.pageobjects.page.HomePage;
import com.atlassian.refapp.pageobjects.component.RefappHeader;

/**
 * Represents the home page of the Reference Application
 */
public class RefappHomePage extends RefappAbstractPage implements HomePage<RefappHeader>
{
    public String getUrl()
    {
       return "";
    }
}
