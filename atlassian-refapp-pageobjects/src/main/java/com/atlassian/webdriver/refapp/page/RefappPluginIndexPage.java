package com.atlassian.webdriver.refapp.page;

import com.google.common.collect.Sets;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Set;

public class RefappPluginIndexPage extends RefappAbstractPage
{
    public String getUrl()
    {
        return "/index.jsp";
    }

    public Set<String> getPluginKeys()
    {
        Set<String> keys = new HashSet<String>();
        for(WebElement element:driver.findElements(By.className("plugin-key")))
        {
            keys.add(element.getText());
        }

        return keys;
    }

    public Set<Bundle> getBundles()
    {
        Set<Bundle> bundles = new HashSet<Bundle>();
        for(WebElement element:driver.findElements(By.className("bundle")))
        {
            String symbolicName = element.findElement(By.className("bundle-symbolic-name")).getText();
            String version = element.findElement(By.className("bundle-version")).getText();
            String state = element.findElement(By.className("bundle-state")).getText();

            Set<String> serviceInterfaces = new HashSet<String>();

            for(WebElement interfaceElement:element.findElements(By.className("bundle-service-interface")))
            {
                serviceInterfaces.add(interfaceElement.getText());
            }

            bundles.add(new Bundle(symbolicName, version, state, serviceInterfaces));
        }

        return bundles;
    }

    public Set<String> getServiceInterfaces()
    {
        Set<String> serviceInterfaces = Sets.newHashSet();
        for(Bundle bundle:getBundles())
        {
            serviceInterfaces = Sets.union(serviceInterfaces, bundle.getServiceInterfaces());
        }

        return serviceInterfaces;
    }

    public class Bundle
    {
        private String symbolicName;
        private String version;
        private String state;
        private Set<String> serviceInterfaces;

        public Bundle(String symbolicName, String version, String state, Set<String> serviceInterfaces)
        {
            this.symbolicName = symbolicName;
            this.version = version;
            this.state = state;
            this.serviceInterfaces = serviceInterfaces;
        }

        public String getSymbolicName()
        {
            return symbolicName;
        }

        public String getVersion()
        {
            return version;
        }

        public String getState()
        {
            return state;
        }

        public Set<String> getServiceInterfaces()
        {
            return serviceInterfaces;
        }
    }
}
