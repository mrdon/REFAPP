package com.atlassian.refapp.auth.internal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlassian.refapp.auth.external.WebSudoSessionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnit44Runner.class)
public class DefaultWebSudoSessionManagerTest
{
    private static final long CURRENT_MILLIS = 1234L;
    private static final String SESS_KEY = DefaultWebSudoSessionManager.class.getName() + "-session";

    private WebSudoSessionManager webSudoSessionManager;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpSession session;

    @Before
    public void setUp() throws Exception
    {

        when(httpServletRequest.getSession(true)).thenReturn(session);
        when(httpServletRequest.getSession(false)).thenReturn(session);
        when(httpServletRequest.getSession()).thenReturn(session);

        webSudoSessionManager = new DefaultWebSudoSessionManager()
        {
            @Override
            long currentTimeMillis()
            {
                return CURRENT_MILLIS;
            }
        };
    }

    @After
    public void tearDown() throws Exception
    {
        webSudoSessionManager = null;
    }

    @Test
    public void isWebSudoSession() throws Exception
    {
        assertThat(webSudoSessionManager.isWebSudoSession(httpServletRequest), is(false));
    }

    @Test
    public void isWebSudoSessionExpired() throws Exception
    {
        when(session.getAttribute(SESS_KEY)).thenReturn(CURRENT_MILLIS - 2);
        assertThat(webSudoSessionManager.isWebSudoSession(httpServletRequest), is(true));
    }

    @Test
    public void isWebSudoSessionTrue() throws Exception
    {
        webSudoSessionManager = new DefaultWebSudoSessionManager()
        {
            @Override
            long currentTimeMillis()
            {
                return CURRENT_MILLIS + TimeUnit.MINUTES.toMillis(12);
            }
        };
        when(session.getAttribute(SESS_KEY)).thenReturn(CURRENT_MILLIS);
        assertThat(webSudoSessionManager.isWebSudoSession(httpServletRequest), is(false));
    }

    @Test
    public void createWebSudoSession() throws Exception
    {
        webSudoSessionManager.createWebSudoSession(httpServletRequest);
        verify(session).setAttribute(SESS_KEY, CURRENT_MILLIS);
    }

    @Test
    public void removeWebSudoSession() throws Exception
    {
        webSudoSessionManager.removeWebSudoSession(httpServletRequest);
        verify(session).removeAttribute(SESS_KEY);
    }
}
