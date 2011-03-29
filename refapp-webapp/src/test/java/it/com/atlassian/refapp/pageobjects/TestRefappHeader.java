package it.com.atlassian.refapp.pageobjects;

import com.atlassian.pageobjects.TestedProductFactory;
import com.atlassian.pageobjects.page.LoginPage;
import com.atlassian.refapp.pageobjects.RefappTestedProduct;
import com.atlassian.refapp.pageobjects.component.RefappHeader;
import com.atlassian.refapp.pageobjects.page.RefappHomePage;
import junit.framework.TestCase;


public class TestRefappHeader extends TestCase
{
    public void testLogin()
    {
        RefappTestedProduct refApp = TestedProductFactory.create(RefappTestedProduct.class);

        RefappHeader header = refApp.visit(LoginPage.class).loginAsSysAdmin(RefappHomePage.class).getHeader();
        assertTrue(header.isAdmin());
        assertTrue(header.isLoggedIn());
    }
}