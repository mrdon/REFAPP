package it.com.atlassian.plugin.refimpl;

import com.atlassian.pageobjects.TestedProductFactory;
import com.atlassian.webdriver.refapp.RefappTestedProduct;
import junit.framework.TestCase;

public class AbstractRefappTestCase extends TestCase
{
    protected static final RefappTestedProduct PRODUCT = TestedProductFactory.create(RefappTestedProduct.class);
}
