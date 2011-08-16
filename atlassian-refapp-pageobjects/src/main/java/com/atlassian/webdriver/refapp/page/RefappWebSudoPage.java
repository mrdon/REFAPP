package com.atlassian.webdriver.refapp.page;

import com.atlassian.pageobjects.Page;
import com.atlassian.pageobjects.PageBinder;
import com.atlassian.pageobjects.binder.WaitUntil;
import com.atlassian.pageobjects.page.WebSudoPage;
import org.openqa.selenium.By;

import javax.inject.Inject;

/**
 * Refapp WebSudo page
 */
public class RefappWebSudoPage extends RefappAbstractPage implements WebSudoPage
{
    @Inject
    private PageBinder pageBinder;

    public <T extends Page> T confirm(Class<T> targetPage)
    {
        return confirm(null, targetPage);
    }

    public <T extends Page> T confirm(String password, Class<T> targetPage)
    {
        driver.findElement(By.name("os_password")).sendKeys(password);
        driver.findElement(By.id("websudo")).submit();

        return pageBinder.navigateToAndBind(targetPage);
    }

    public String getUrl()
    {
        return "/plugins/servlet/websudo";
    }

    public boolean isRequestAccessMessagePresent()
    {
        return driver.elementExists(By.id("request-access-message"));
    }
}
