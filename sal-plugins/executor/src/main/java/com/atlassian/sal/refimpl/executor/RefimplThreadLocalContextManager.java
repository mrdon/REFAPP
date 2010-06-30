package com.atlassian.sal.refimpl.executor;

import com.atlassian.sal.core.executor.ThreadLocalContextManager;
import com.atlassian.seraph.auth.AuthenticationContext;

import java.security.Principal;

public class RefimplThreadLocalContextManager implements ThreadLocalContextManager
{
    private final AuthenticationContext authenticationContext;

    public RefimplThreadLocalContextManager(AuthenticationContext authenticationContext)
    {
        this.authenticationContext = authenticationContext;
    }

    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    public Object getThreadLocalContext()
    {
        return authenticationContext.getUser();
    }

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    public void setThreadLocalContext(Object context)
    {
        authenticationContext.setUser((Principal) context);
    }

    /**
     * Clear the thread local context on the current thread
     */
    public void clearThreadLocalContext()
    {
        authenticationContext.setUser(null);
    }
}
