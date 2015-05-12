package com.ndpmedia.rocketmq.ip;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
public interface IPMappingService {

    @GET
    @Path("/ip")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    String lookUp(@FormParam("innerIP") String innerIP);
}
