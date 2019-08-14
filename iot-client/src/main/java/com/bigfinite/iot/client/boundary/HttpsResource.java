package com.bigfinite.iot.client.boundary;

import java.time.LocalDateTime;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/https")
public class HttpsResource {

    @ConfigProperty(name = "server.base.url")
    String serverBaseUrl;

    @GET
    @Path("/publish")
    @Produces(MediaType.TEXT_PLAIN)
    public String sendMessage() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serverBaseUrl + "/https");
        Entity<String> entity = Entity.entity("Hello HTTPS world!", MediaType.TEXT_PLAIN_TYPE);
        Response response = target.request().post(entity);
        return "HTTPS message published at " + LocalDateTime.now() + " with response : " + response.getStatus();
    }

}
