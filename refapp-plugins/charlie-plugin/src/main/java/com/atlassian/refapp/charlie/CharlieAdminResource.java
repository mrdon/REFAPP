package com.atlassian.refapp.charlie;

import org.codehaus.jettison.json.JSONArray;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * REST resource for Charlie administration.
 */
@Path ("admin")
@Consumes (MediaType.APPLICATION_JSON)
@Produces (MediaType.APPLICATION_JSON)
public class CharlieAdminResource
{
    private final CharlieStore store;

    public CharlieAdminResource(CharlieStore store)
    {
        this.store = store;
    }

    /**
     * Returns the Charlies.
     *
     * @return a JSON array of Charlie keys
     */
    @GET
    public String listCharlies()
    {
        JSONArray result = new JSONArray();
        for (String charlies : store.getCharlies())
        {
            result.put(charlies);
        }

        return result.toString();
    }

    /**
     * Creates a Charlie.
     *
     * @param key a String containing the Charlie key
     * @param name a String containing the Charlie name
     */
    @Path("{key}/{name}")
    @POST
    public void createCharlie(@PathParam ("key") String key, @PathParam ("name") String name)
    {
        if (isBlank(key) || isBlank(name))
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        List<String> charlies = store.getCharlies();
        charlies.add(key);
        store.storeCharlies(charlies);
        store.setCharlieName(key, name);
    }
}
