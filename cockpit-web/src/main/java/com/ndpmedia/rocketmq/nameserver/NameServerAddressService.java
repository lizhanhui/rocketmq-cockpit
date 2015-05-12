package com.ndpmedia.rocketmq.nameserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
public interface NameServerAddressService {

    @GET
    @Path("/nsaddr")
    String listNameServer();
}
