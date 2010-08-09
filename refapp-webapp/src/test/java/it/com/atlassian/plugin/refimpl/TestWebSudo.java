package it.com.atlassian.plugin.refimpl;

public class TestWebSudo extends AbstractRefappTestCase
{

    private static final String TEST_USER = "admin";
    private static final String TEST_PASS = "admin";

    public TestWebSudo(String name)
    {
        super(name);
    }

    public void testWebSudoRequired()
    {
        loginAs(TEST_USER, TEST_PASS);

        // Redirect to the WebSudo form
        gotoPage("/plugins/servlet/charlieadmin");
        
        assertTextPresent("You have requested access to an administrative function");

        // Authenticate
        setTextField("os_password", TEST_PASS);
        clickButton("websudo");

        // Redirect to the charlie admin screen
        assertTextPresent("Charlies Administration");
    }

    public void testWebSudoRequiredWrongPassword()
    {
        loginAs(TEST_USER, TEST_PASS);

        // Redirect to the WebSudo form
        gotoPage("/plugins/servlet/charlieadmin");

        assertTextPresent("You have requested access to an administrative function");

        // Authenticate
        setTextField("os_password", "");
        clickButton("websudo");

        assertTextPresent("You have requested access to an administrative function");
    }

    private void loginAs(final String user, final String password)
    {
        beginAt("/"); // Starts a new session
        assertLinkPresentWithText("Login");
        clickLinkWithExactText("Login");
        assertTextPresent("Username");
        setTextField("os_username", user);
        setTextField("os_password", password);
        uncheckCheckbox("os_websudo");
        clickButton("os_login");
        assertTextPresent("admin");
        assertButtonPresent("logout");
    }
}