package com.bigfinite.iot.server.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/https")
public class HttpsResource {

    static final Logger LOGGER = Logger.getLogger(HttpsResource.class.getName());

    @POST
    public void handleMessage(String message) {
        LOGGER.log(Level.INFO, "Handle HTTPS message :: {0}", message);
    }

}
