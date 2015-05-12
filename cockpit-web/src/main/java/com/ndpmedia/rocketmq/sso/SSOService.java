package com.ndpmedia.rocketmq.sso;

import com.ndpmedia.rocketmq.cockpit.model.Login;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(value = MediaType.APPLICATION_JSON)
public interface SSOService {

    @GET
    @Path("/sso/{token}")
    Login authenticate(@PathParam("token") String token);
}
