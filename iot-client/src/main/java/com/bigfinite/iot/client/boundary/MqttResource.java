package com.bigfinite.iot.client.boundary;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/mqtt")
public class MqttResource {

    @Inject
    MqttPublisher publisher;

    @GET
    @Path("/publish")
    @Produces(MediaType.TEXT_PLAIN)
    public String sendMessage(@QueryParam("message") String message) throws Exception {
        publisher.publish(Optional.ofNullable(message).orElse("Hello MQTT world!"));
        return "MQTT message published at " + LocalDateTime.now();
    }

}
