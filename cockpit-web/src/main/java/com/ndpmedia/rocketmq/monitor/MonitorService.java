package com.ndpmedia.rocketmq.monitor;

import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
public interface MonitorService {

    @GET
    @Path("/monitor/")
    String monitor();

    @GET
    @Path("/monitor/{consumerGroup}")
    List<ConsumeProgress> monitor(@PathParam("consumerGroup") String consumerGroup);

    @GET
    @Path("/monitor/{consumerGroup}/{topic}")
    List<ConsumeProgress> monitor(@PathParam("consumerGroup") String consumerGroup, @PathParam("topic") String topic);
}
