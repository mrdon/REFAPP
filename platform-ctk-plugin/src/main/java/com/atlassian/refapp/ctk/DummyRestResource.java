package com.atlassian.refapp.ctk;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Singleton
@AnonymousAllowed
@Path("/dummy")
public class DummyRestResource
{
    @GET
    @Path("hello")
    public Response getHelloWorld(@QueryParam("who") String who)
    {
        if (who == null)
        {
            return Response.ok(new Result()).build();
        }
        else
        {
            return Response.ok(new Result("hello " + who + "!")).build();
        }
    }

    @XmlRootElement(name = "result")
    public static class Result
    {
        @XmlAttribute
        private final String message;

        private Result()
        {
            this.message = "hello world!";
        }

        private Result(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }
}
