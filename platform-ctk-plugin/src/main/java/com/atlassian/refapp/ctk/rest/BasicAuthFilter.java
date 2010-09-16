package com.atlassian.refapp.ctk.rest;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * Used for basic authentication in the REST tests. Shamelessly copied from the UPM test source tree.
 */
public final class BasicAuthFilter extends ClientFilter
{
    private final String auth;

    public BasicAuthFilter(String password, String username)
    {
        try
        {
            auth = "Basic " + new String(encodeBase64((username + ":" + password).getBytes("ASCII")));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("That's some funky JVM you've got there", e);
        }
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException
    {
        MultivaluedMap<String, Object> headers = cr.getMetadata();
        if (!headers.containsKey(AUTHORIZATION))
        {
            headers.add(AUTHORIZATION, auth);
        }
        return getNext().handle(cr);
    }
}