package com.atlassian.refapp.auth.internal;

import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;
import com.atlassian.user.security.password.PasswordEncryptor;

public final class Services
{
    private Services() {}

    private static UserManager userManager;
    private static PasswordEncryptor passwordEncryptor;
    private static GroupManager groupManager;
    
    static UserManager getUserManager()
    {
        return userManager;
    }
    
    static void setUserManager(UserManager userManager)
    {
        Services.userManager = userManager;
    }

    static PasswordEncryptor getPasswordEncryptor()
    {
        return passwordEncryptor;
    }
    
    static void setPasswordEncryptor(PasswordEncryptor passwordEncryptor)
    {
        Services.passwordEncryptor = passwordEncryptor;
    }

    public static GroupManager getGroupManager()
    {
        return groupManager;
    }
    
    public static void setGroupManager(GroupManager groupManager)
    {
        Services.groupManager = groupManager;
    }
}
