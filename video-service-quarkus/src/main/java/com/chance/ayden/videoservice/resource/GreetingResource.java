package com.chance.ayden.videoservice.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    @GET
    public Map<String, Object> index() {
        return Collections.singletonMap("message", "Hello World");
    }
}
