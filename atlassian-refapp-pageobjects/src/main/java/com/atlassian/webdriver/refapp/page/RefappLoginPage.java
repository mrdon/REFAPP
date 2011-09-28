package com.atlassian.webdriver.refapp.page;

import com.atlassian.pageobjects.PageBinder;
import com.atlassian.pageobjects.elements.PageElementFinder;
import com.atlassian.pageobjects.page.HomePage;
import com.atlassian.pageobjects.page.LoginPage;
import com.atlassian.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.inject.Inject;

/**
 *
 */
public class RefappLoginPage extends RefappAbstractPage implements LoginPage
{
    @Inject
    PageBinder pageBinder;

    @Inject
    PageElementFinder elementFinder;

    public String getUrl()
    {
        return "/plugins/servlet/login";
    }

    /**
     * Is the user being prompted for login. This may be false if we visit the page while the user is already logged in.
     */
    public boolean isPromptingForLogin()
    {
        return elementFinder.find(By.name("os_username")).isPresent();
    }

    public <M extends Page> M login(String username, String password, Class<M> nextPage)
    {
        driver.findElement(By.name("os_username")).sendKeys(username);
        driver.findElement(By.name("os_password")).sendKeys(password);
        driver.findElement(By.id("os_login")).submit();

        return bindNextPage(nextPage);
    }

    /**
     * Logins into refapp.
     *
     * @param bypassWebsudo if true, user will not have to deal with websudo screen again until the automatically given privilege is expired
     */
    public <M extends Page> M login(String username, String password, boolean bypassWebsudo, Class<M> nextPage)
    {
        driver.findElement(By.name("os_username")).sendKeys(username);
        driver.findElement(By.name("os_password")).sendKeys(password);
        WebElement webSudoCheckBox = driver.findElement(By.name("os_websudo"));

        if (bypassWebsudo)
        {
            if (!webSudoCheckBox.isSelected()) {
                webSudoCheckBox.click();
            }
        }
        else if (webSudoCheckBox.isSelected())
        {
            webSudoCheckBox.click();
        }

        driver.findElement(By.id("os_login")).submit();

        return bindNextPage(nextPage);
    }

    private <M extends Page> M bindNextPage(Class<M> nextPage)
    {
        return HomePage.class.isAssignableFrom(nextPage) ? pageBinder.bind(nextPage) : pageBinder.navigateToAndBind(nextPage);
    }

    public <M extends Page> M loginAsSysAdmin(Class<M> nextPage)
    {
        return login("admin", "admin", nextPage);
    }

}
