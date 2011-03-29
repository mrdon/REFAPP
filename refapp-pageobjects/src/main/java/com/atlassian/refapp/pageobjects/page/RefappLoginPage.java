package com.atlassian.refapp.pageobjects.page;

import com.atlassian.pageobjects.Page;
import com.atlassian.pageobjects.PageBinder;
import com.atlassian.pageobjects.page.HomePage;
import com.atlassian.pageobjects.page.LoginPage;
import org.openqa.selenium.By;

import javax.inject.Inject;

/**
 * Represents the login page of the Reference Application
 */
public class RefappLoginPage extends RefappAbstractPage implements LoginPage
{
    @Inject
    PageBinder pageBinder;

    public String getUrl()
    {
        return "/plugins/servlet/login";
    }

    public <M extends Page> M login(String username, String password, Class<M> nextPage)
    {
        driver.findElement(By.name("os_username")).sendKeys(username);
        driver.findElement(By.name("os_password")).sendKeys(password);
        driver.findElement(By.id("os_login")).submit();

        return HomePage.class.isAssignableFrom(nextPage) ? pageBinder.bind(nextPage) : pageBinder.navigateToAndBind(nextPage);
    }


    public <M extends Page> M loginAsSysAdmin(Class<M> nextPage)
    {
        return login("admin", "admin", nextPage);
    }

}
