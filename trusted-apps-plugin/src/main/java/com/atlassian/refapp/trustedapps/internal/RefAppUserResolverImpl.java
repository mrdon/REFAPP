package com.atlassian.refapp.trustedapps.internal;

import java.security.Principal;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.UserResolver;
import com.atlassian.user.EntityException;
import com.atlassian.user.UserManager;

public class RefAppUserResolverImpl implements UserResolver
{
    private final UserManager userManager;
    
    public RefAppUserResolverImpl(UserManager userManager)
    {
        this.userManager = userManager;
    }
    
    public Principal resolve(ApplicationCertificate certificate)
    {
        try
        {
            return userManager.getUser(certificate.getUserName());
        }
        catch (EntityException e)
        {
            return null;
        }
    }
}
