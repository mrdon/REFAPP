package it.com.atlassian.webdriver.refapp;

import com.atlassian.pageobjects.TestedProductFactory;
import com.atlassian.pageobjects.page.LoginPage;
import com.atlassian.webdriver.refapp.RefappTestedProduct;
import com.atlassian.webdriver.refapp.component.RefappHeader;
import com.atlassian.webdriver.refapp.page.RefappHomePage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestRefappHeader
{
    private static final RefappTestedProduct REFAPP = TestedProductFactory.create(RefappTestedProduct.class);

    @Test
    public void testLogin()
    {
        RefappHeader header = REFAPP.visit(LoginPage.class).loginAsSysAdmin(RefappHomePage.class).getHeader();
        assertTrue(header.isAdmin());
        assertTrue(header.isLoggedIn());
    }

    @Test
    public void testLogout()
    {
        RefappHeader header = REFAPP.visit(LoginPage.class).loginAsSysAdmin(RefappHomePage.class).getHeader();
        header.logout(LoginPage.class);

        assertFalse(header.isLoggedIn());
    }

    @After
    @Before
    public void logout()
    {
        REFAPP.gotoHomePage().getHeader().logout(LoginPage.class);
    }
}