package com.atlassian.webdriver.refapp.component;

import javax.inject.Inject;

import com.atlassian.pageobjects.Page;
import com.atlassian.pageobjects.PageBinder;
import com.atlassian.pageobjects.component.Header;
import com.atlassian.pageobjects.page.HomePage;
import com.atlassian.webdriver.AtlassianWebDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 *
 */
public class RefappHeader implements Header
{
    private static final By LOGIN = By.id("login");

    @Inject
    protected AtlassianWebDriver driver;

    @Inject
    protected PageBinder pageBinder;

    public boolean isLoggedIn()
    {
        return driver.findElement(LOGIN).getText().equals("Logout");
    }

    public boolean isAdmin()
    {
        return isLoggedIn() && driver.findElement(By.id("user")).getText().contains("(Sysadmin)");
    }

    /**
     * Gets websudo banner message.
     *
     * @return message, or null if the banner doesn't exist on the screen
     */
    public String getWebSudoBannerMessage()
    {
        try
        {
            WebElement banner = driver.findElement(By.id("websudo-banner"));
            return banner.getText();
        }
        catch (NoSuchElementException nsee)
        {
            return null;
        }
    }

    public <M extends Page> M dropWebSudo(Class<M> nextPage)
    {
        driver.findElement(By.id("websudo-drop")).click();
        return HomePage.class.isAssignableFrom(nextPage) ? pageBinder.bind(nextPage) : pageBinder.navigateToAndBind(nextPage);
    }

    public <M extends Page> M logout(Class<M> nextPage)
    {
        if (isLoggedIn()) {
            driver.findElement(LOGIN).click();
        }
        return HomePage.class.isAssignableFrom(nextPage) ? pageBinder.bind(nextPage) : pageBinder.navigateToAndBind(nextPage);
    }
}
