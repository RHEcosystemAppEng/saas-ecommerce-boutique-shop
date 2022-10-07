package org.acme.saas.controller;

import io.vertx.core.json.JsonObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/manager")
public class ManagerResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(JsonObject requestBody) {
        String email = requestBody.getString("email");
        String password = requestBody.getString("password");

        if (email != null && email.equals("admin") &&
                password != null && password.equals("redhat")) {
            JsonObject respObj = new JsonObject();
            respObj.put("loggedInUserName", "Tenant Manager");
            return Response.ok(respObj).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
