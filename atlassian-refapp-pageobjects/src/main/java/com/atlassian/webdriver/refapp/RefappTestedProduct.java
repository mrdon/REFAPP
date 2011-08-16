package com.atlassian.webdriver.refapp;

import com.atlassian.pageobjects.Defaults;
import com.atlassian.pageobjects.Page;
import com.atlassian.pageobjects.PageBinder;
import com.atlassian.pageobjects.ProductInstance;
import com.atlassian.pageobjects.TestedProduct;
import com.atlassian.pageobjects.TestedProductFactory;
import com.atlassian.pageobjects.binder.InjectPageBinder;
import com.atlassian.pageobjects.binder.StandardModule;
import com.atlassian.pageobjects.component.Header;
import com.atlassian.pageobjects.elements.ElementModule;
import com.atlassian.pageobjects.elements.timeout.TimeoutsModule;
import com.atlassian.pageobjects.page.AdminHomePage;
import com.atlassian.pageobjects.page.HomePage;
import com.atlassian.pageobjects.page.LoginPage;
import com.atlassian.pageobjects.page.WebSudoPage;
import com.atlassian.webdriver.AtlassianWebDriverModule;
import com.atlassian.webdriver.pageobjects.DefaultWebDriverTester;
import com.atlassian.webdriver.pageobjects.WebDriverTester;
import com.atlassian.webdriver.refapp.component.RefappHeader;
import com.atlassian.webdriver.refapp.page.RefappAdminHomePage;
import com.atlassian.webdriver.refapp.page.RefappHomePage;
import com.atlassian.webdriver.refapp.page.RefappLoginPage;
import com.atlassian.webdriver.refapp.page.RefappWebSudoPage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
@Defaults(instanceId = "refapp", contextPath = "/refapp", httpPort = 5990)
public class RefappTestedProduct implements TestedProduct<WebDriverTester>
{
    private final PageBinder pageBinder;
    private final WebDriverTester webDriverTester;
    private final ProductInstance productInstance;

    public RefappTestedProduct(TestedProductFactory.TesterFactory<WebDriverTester> testerFactory, ProductInstance productInstance)
    {
        checkNotNull(productInstance);
        WebDriverTester tester = null;
        if (testerFactory == null)
        {
            tester = new DefaultWebDriverTester();
        }
        else
        {
            tester = testerFactory.create();
        }
        this.webDriverTester = tester;
        this.productInstance = productInstance;
        this.pageBinder = new InjectPageBinder(productInstance, tester, new StandardModule(this),
                new AtlassianWebDriverModule(this), new ElementModule(), new TimeoutsModule());

        this.pageBinder.override(Header.class, RefappHeader.class);
        this.pageBinder.override(HomePage.class, RefappHomePage.class);
        this.pageBinder.override(AdminHomePage.class, RefappAdminHomePage.class);
        this.pageBinder.override(LoginPage.class, RefappLoginPage.class);
        this.pageBinder.override(WebSudoPage.class, RefappWebSudoPage.class);
    }

    public RefappHomePage gotoHomePage()
    {
        return pageBinder.navigateToAndBind(RefappHomePage.class);
    }

    public RefappAdminHomePage gotoAdminHomePage()
    {
        return pageBinder.navigateToAndBind(RefappAdminHomePage.class);
    }

    public RefappLoginPage gotoLoginPage()
    {
        return pageBinder.navigateToAndBind(RefappLoginPage.class);
    }

    public <P extends Page> P visit(Class<P> pageClass, Object... args)
    {
        return pageBinder.navigateToAndBind(pageClass, args);
    }

    public PageBinder getPageBinder()
    {
        return pageBinder;
    }

    public ProductInstance getProductInstance()
    {
        return productInstance;
    }

    public WebDriverTester getTester()
    {
        return webDriverTester;
    }
}
