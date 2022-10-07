package org.acme.saas.controller;

import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.reactive.RestForm;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ops")
public class OpsResource {

    @POST
    @Path("/login")
    public Response login(@RestForm String email, @RestForm String password) {
        if (email.equals("admin") && password.equals("redhat")) {
            JsonObject respObj = new JsonObject();
            respObj.put("loggedInUserName", "Tenant Manager");
            return Response.ok(respObj).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
