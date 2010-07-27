package it.com.atlassian.plugin.refimpl;

public class TestLogin extends AbstractRefappTestCase
{
    public TestLogin(String name)
    {
        super(name);
    }

    public void testAdminLogin()
    {
        beginAt("/");
        assertLinkPresentWithText("Login");
        clickLinkWithExactText("Login");
        assertTextPresent("Username");
        setTextField("os_username", "admin");
        setTextField("os_password", "admin");
        clickButton("os_login");
        assertTextPresent("admin");
        assertButtonPresent("logout");
    }
}
