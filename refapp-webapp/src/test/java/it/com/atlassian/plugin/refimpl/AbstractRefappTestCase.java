package it.com.atlassian.plugin.refimpl;

import com.atlassian.plugin.refimpl.ParameterUtils;
import com.atlassian.plugin.webresource.UrlMode;
import net.sourceforge.jwebunit.junit.WebTestCase;

/**
 *
 */
public class AbstractRefappTestCase extends WebTestCase
{
    public AbstractRefappTestCase(String name)
    {
        super(name);
    }

    public void setUp() throws Exception {
        getTestContext().setBaseUrl(ParameterUtils.getBaseUrl(UrlMode.ABSOLUTE));
    }
}
