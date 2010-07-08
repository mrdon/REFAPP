package com.atlassian.refapp.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class WhoamiApplinksServlet extends HttpServlet
{
    private static final String TEMPLATE = "/templates/whoami.vm";
    private static final String ENDPOINT = "/plugins/servlet/applinks/whoami";

    private final TemplateRenderer templateRenderer;
    private final ApplicationLinkService applicationLinkService;
    private final WebResourceManager webResourceManager;

    public WhoamiApplinksServlet(final ApplicationLinkService applicationLinkService,
                                 final TemplateRenderer templateRenderer,
                                 final WebResourceManager webResourceManager)
    {
        this.templateRenderer = templateRenderer;
        this.applicationLinkService = applicationLinkService;
        this.webResourceManager = webResourceManager;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        final ImmutableMap.Builder<String, Object> contextBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<ApplicationLink, AuthenticationInformation> recordBuilder = ImmutableMap.builder();

        for (ApplicationLink link : applicationLinkService.getApplicationLinks())
        {
            recordBuilder.put(link, getAuthenticationInformation(request, link));
        }

        contextBuilder.put("records", recordBuilder.build());
        render(TEMPLATE, contextBuilder.build(), response);
    }

    private AuthenticationInformation getAuthenticationInformation(final HttpServletRequest request, final ApplicationLink link)
    {
        final ApplicationLinkRequestFactory requestFactory = link.createAuthenticatedRequestFactory();
        final AtomicReference<String> username = new AtomicReference<String>();
        final AtomicReference<AuthenticationStatus> status = new AtomicReference<AuthenticationStatus>(AuthenticationStatus.COMMUNICATION_ERROR);
        final AtomicReference<String> errorMessage = new AtomicReference<String>();
        try
        {
            requestFactory
                    .createRequest(Request.MethodType.GET, ENDPOINT)
                    .execute(new ResponseHandler()
                    {
                        public void handle(final Response response) throws ResponseException
                        {
                            if (response.isSuccessful())
                            {
                                final String body = response.getResponseBodyAsString();
                                if (body == null || "".equals(body))
                                {
                                    status.set(AuthenticationStatus.ANONYMOUS);
                                }
                                else
                                {
                                    username.set(body);
                                    status.set(AuthenticationStatus.ACCEPTED);
                                }
                            }
                            else
                            {
                                status.set(AuthenticationStatus.COMMUNICATION_ERROR);
                                errorMessage.set(String.format("%s: %s", response.getStatusCode(), response.getStatusText()));
                            }
                        }
                    });

        }
        catch (CredentialsRequiredException cre)
        {
            status.set(AuthenticationStatus.CREDENTIALS_REQUIRED);
        }
        catch (ResponseException re)
        {
            status.set(AuthenticationStatus.COMMUNICATION_ERROR);
            errorMessage.set(re.getMessage());
        }
        final String authorisationURL = requestFactory.getAuthorisationURI() == null ? null :
                requestFactory.getAuthorisationURI(getCurrentLocation(request)).toString();
        return new AuthenticationInformation(username.get(), errorMessage.get(), authorisationURL, status.get());
    }

    private URI getCurrentLocation(final HttpServletRequest request)
    {
        try
        {
            return new URI(request.getRequestURL().toString());
        }
        catch (URISyntaxException e)
        {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    private void render(final String template, Map<String, Object> context, final HttpServletResponse response)
            throws IOException
    {
        response.setContentType("text/html; charset=utf-8");
        webResourceManager.requireResource("com.atlassian.auiplugin:ajs");
        templateRenderer.render(template, context, response.getWriter());
    }

    public static class AuthenticationInformation
    {
        private final AuthenticationStatus status;
        private final String username;
        private final String errorMessage;
        private final String authorisationURL;

        public AuthenticationInformation(String username, String errorMessage, String authorisationURL, AuthenticationStatus status)
        {
            this.errorMessage = errorMessage;
            this.authorisationURL = authorisationURL;
            this.status = status;
            this.username = username;
        }

        public String getAuthorisationURL()
        {
            return authorisationURL;
        }

        public String getErrorMessage()
        {
            return errorMessage;
        }

        public String getStatus()
        {
            return status.name();
        }

        /**
         * {@code null} implies anonymous.
         */
        public String getUsername()
        {
            return username;
        }
    }

    public static enum AuthenticationStatus
    {
        ACCEPTED,
        CREDENTIALS_REQUIRED,
        COMMUNICATION_ERROR,
        ANONYMOUS
    }
}
