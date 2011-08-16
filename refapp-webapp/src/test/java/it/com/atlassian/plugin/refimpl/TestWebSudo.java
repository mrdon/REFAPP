package it.com.atlassian.plugin.refimpl;

import com.atlassian.webdriver.refapp.page.RefappAdminHomePage;
import com.atlassian.webdriver.refapp.page.RefappCharlieAdminPage;
import com.atlassian.webdriver.refapp.page.RefappHomePage;
import com.atlassian.webdriver.refapp.page.RefappLoginPage;
import com.atlassian.webdriver.refapp.page.RefappWebSudoPage;

public class TestWebSudo extends AbstractRefappTestCase
{

    private static final String TEST_USER = "admin";
    private static final String TEST_PASS = "admin";

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        PRODUCT.gotoHomePage().getHeader().logout(RefappHomePage.class);
    }

    public void testWebSudoByPass()
    {
        loginAs(TEST_USER, TEST_PASS, true);
        RefappCharlieAdminPage charlieAdminPage = PRODUCT.visit(RefappCharlieAdminPage.class);
        assertTrue(charlieAdminPage.getHeader().getWebSudoBannerMessage().contains("You have temporary access to administrative functions."));
    }

    public void testWebSudoRequired()
    {
        loginAs(TEST_USER, TEST_PASS);

        PRODUCT.visit(RefappCharlieAdminPage.class);
        RefappWebSudoPage webSudoPage = PRODUCT.getPageBinder().bind(RefappWebSudoPage.class);
        RefappCharlieAdminPage charlieAdminPage = webSudoPage.confirm(TEST_PASS, RefappCharlieAdminPage.class);
        assertTrue(charlieAdminPage.getHeader().getWebSudoBannerMessage().contains("You have temporary access to administrative functions."));
    }

    public void testWebSudoRequiredWrongPassword()
    {
        loginAs(TEST_USER, TEST_PASS);

        PRODUCT.visit(RefappCharlieAdminPage.class);
        RefappWebSudoPage webSudoPage = PRODUCT.getPageBinder().bind(RefappWebSudoPage.class);
        webSudoPage = webSudoPage.confirm("", RefappWebSudoPage.class);
        webSudoPage = webSudoPage.confirm("blah", RefappWebSudoPage.class);
        assertTrue(webSudoPage.isRequestAccessMessagePresent());
    }

    public void testWebSudoStateIsShown()
    {
        loginAs(TEST_USER, TEST_PASS, true);
        RefappCharlieAdminPage charlieAdminPage = PRODUCT.visit(RefappCharlieAdminPage.class);
        assertTrue(charlieAdminPage.getHeader().getWebSudoBannerMessage().contains("You have temporary access to administrative functions."));
    }

    public void testWebSudoPrivilegesCanBeDropped()
    {
        loginAs(TEST_USER, TEST_PASS, true);
        RefappCharlieAdminPage charlieAdminPage = PRODUCT.visit(RefappCharlieAdminPage.class);
        assertTrue(charlieAdminPage.getHeader().getWebSudoBannerMessage().contains("You have temporary access to administrative functions."));

        RefappHomePage homePage = charlieAdminPage.getHeader().dropWebSudo(RefappHomePage.class);
        assertNull(homePage.getHeader().getWebSudoBannerMessage());
    }

    private RefappAdminHomePage loginAs(final String user, final String password)
    {
        RefappLoginPage loginPage = PRODUCT.gotoLoginPage();
        return loginPage.login(user, password, false, RefappAdminHomePage.class);
    }

    private RefappAdminHomePage loginAs(final String user, final String password, boolean bypassWebsudo)
    {
        RefappLoginPage loginPage = PRODUCT.gotoLoginPage();
        return loginPage.login(user, password, bypassWebsudo, RefappAdminHomePage.class);
    }
}