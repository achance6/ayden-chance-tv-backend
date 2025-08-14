package com.chance.ayden.videoservice.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingController {

    @GET
    public Map<String, Object> index() {
        return Collections.singletonMap("message", "Hello World");
    }
}
