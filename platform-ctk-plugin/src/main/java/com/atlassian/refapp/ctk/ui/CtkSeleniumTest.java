package com.atlassian.refapp.ctk.ui;

import java.util.List;

import com.atlassian.functest.selenium.webdriver.AbstractSeleniumTestCase;
import com.atlassian.refapp.ctk.AppSpecificInfoProvider;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;

public class CtkSeleniumTest extends AbstractSeleniumTestCase
{
    private AppSpecificInfoProvider infoProvider;

 	public void setInfoProvider(AppSpecificInfoProvider infoProvider)
 	{
 	    this.infoProvider = infoProvider;
 	}

    protected AppSpecificInfoProvider getInfoProvider()
    {
        return infoProvider;
    }

    public void assertTextPresent(String text)
    {
        assertTrue("Should contain '" + text + "'", getDriver().getPageSource().contains(text));
    }

    public void assertTitle(String title)
    {
        assertTrue("Should have title '" + title + "'", getDriver().getTitle().equals(title));
    }

    public void click(By by)
    {
        findElement(by).click();
    }

    public WebElement findElement(By by)
    {
        try
        {
            return getDriver().findElement(by);
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    public List<WebElement> findElements(By by)
    {
        try
        {
            return getDriver().findElements(by);
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    public boolean hasElement(By by)
    {
        return findElement(by) != null;
    }

    private void pause() throws InterruptedException
    {
        Thread.sleep(1000);
    }
}
