package com.bigfinite.iot.client.boundary;

import com.bigfinite.iot.client.utils.SSLUtils;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

    @ConfigProperty(name = "cert.public-key")
    String publicKey;
    @ConfigProperty(name = "cert.private-key")
    String privateKey;
    @ConfigProperty(name = "cert.password")
    String password;

    @GET
    @Path("/publish")
    @Produces(MediaType.TEXT_PLAIN)
    public String sendMessage(@QueryParam("message") String message)
            throws NoSuchAlgorithmException, IOException, Exception {
        final Client client = ClientBuilder.newBuilder()
                .sslContext(SSLUtils.getSSLContext(publicKey, privateKey, password))
                .build();
        WebTarget target = client.target(serverBaseUrl + "/https");
        Entity<String> entity = buildMessage(Optional.ofNullable(message));
        Response response = target.request().post(entity);
        return new StringBuilder("HTTPS message published at ")
                .append(LocalDateTime.now())
                .append(" with response : ")
                .append(response.getStatus()).toString();
    }

    private Entity<String> buildMessage(Optional<String> message) {
        return Entity.entity(message.orElse("Hello HTTPS world!"), MediaType.TEXT_PLAIN_TYPE);
    }

}
