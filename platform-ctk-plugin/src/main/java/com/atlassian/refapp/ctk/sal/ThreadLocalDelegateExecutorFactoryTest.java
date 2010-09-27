package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.user.UserManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * The thread local delegate executor factory should at least transfer the user state
 */
public class ThreadLocalDelegateExecutorFactoryTest extends SpringAwareTestCase
{
    private ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    private UserManager userManager;

    public void setThreadLocalDelegateExecutorFactory(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory)
    {
        this.threadLocalDelegateExecutorFactory = threadLocalDelegateExecutorFactory;
    }

    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

    @Test
    public void testUserManagerAvailable()
    {
        assertNotNull("ThreadLocalDelegateExecutorFactory should be available to plugins", threadLocalDelegateExecutorFactory);
    }

    @Test
    public void testExecution() throws ExecutionException, InterruptedException
    {
        assertTrue("UserManager should be injectable", userManager != null);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService wrappedExecutorService = threadLocalDelegateExecutorFactory.createExecutorService(
            executorService);
        Callable<String> userRetriever = new Callable<String>()
        {
            public String call() throws Exception
            {
                return userManager.getRemoteUsername();
            }
        };

        String userInCallingThread = userManager.getRemoteUsername();
        String userInExecutorThread = wrappedExecutorService.submit(userRetriever).get();
        if (userInCallingThread != null)
        {
            assertTrue(
                "User in executor thread not equal to user in calling thread, expected: '" + userInCallingThread +
                    "' but was '" + userInExecutorThread + "'.", userInCallingThread.equals(userInExecutorThread));
        }
        // Check that the wrapping executor cleaned up after itself, by running the same check on the unwrapped executor
        assertTrue("Executor thread not cleaned up", executorService.submit(userRetriever).get() == null);
    }
}
