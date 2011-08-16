package com.atlassian.webdriver.refapp.page;

import com.atlassian.pageobjects.Page;
import com.atlassian.pageobjects.PageBinder;
import com.atlassian.pageobjects.binder.WaitUntil;
import com.atlassian.webdriver.AtlassianWebDriver;
import com.atlassian.webdriver.refapp.component.RefappHeader;
import org.openqa.selenium.By;

import javax.inject.Inject;

public abstract class RefappAbstractPage implements Page
{
    @Inject
    protected AtlassianWebDriver driver;

    @Inject
    protected PageBinder pageBinder;

    @WaitUntil
    public void doWait()
    {
        driver.waitUntilElementIsLocated(By.className("refapp-footer"));
    }

    public RefappHeader getHeader()
    {
        return pageBinder.bind(RefappHeader.class);
    }
}
