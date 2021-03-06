package com.atlassian.refapp.sal.executor;

import com.atlassian.sal.core.executor.DefaultThreadLocalDelegateExecutorFactory;
import com.atlassian.seraph.auth.AuthenticationContext;

public class RefimplThreadLocalDelegateExecutorFactory extends DefaultThreadLocalDelegateExecutorFactory
{
    public RefimplThreadLocalDelegateExecutorFactory(AuthenticationContext authenticationContext)
    {
        super(new RefimplThreadLocalContextManager(authenticationContext));
    }
}
