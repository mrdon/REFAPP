package it.com.atlassian.plugin.refimpl;

import com.atlassian.webdriver.refapp.page.RefappHomePage;
import com.atlassian.webdriver.refapp.page.RefappLoginPage;

public class TestLogin extends AbstractRefappTestCase
{
    private RefappLoginPage loginPage;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        loginPage = PRODUCT.gotoLoginPage();
    }

    public void testBadAdminLogin()
    {
        // bad login.
        loginPage = loginPage.login("blahuser", "blahpassword", RefappLoginPage.class);
        assertFalse("The login must have failed.", loginPage.getHeader().isLoggedIn());

        // logout.
        RefappHomePage homePage = loginPage.getHeader().logout(RefappHomePage.class);
        assertFalse("Must have logged out.", homePage.getHeader().isLoggedIn());
    }

    public void testGoodAdminLogin()
    {
        // good login.
        RefappHomePage homePage = loginPage.login("admin", "admin", RefappHomePage.class);
        assertTrue("The login must have been successful.", homePage.getHeader().isLoggedIn());
        assertTrue("Must have logged in as admin.", homePage.getHeader().isAdmin());

        // logout.
        homePage = homePage.getHeader().logout(RefappHomePage.class);
        assertFalse("Must have logged out.", homePage.getHeader().isLoggedIn());
    }
}
