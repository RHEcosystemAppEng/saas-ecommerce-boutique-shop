package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.TokenData;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/manager")
public class ManagerResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> login(LoginData loginData) {

        if (loginData != null && loginData.getEmail() != null && loginData.getEmail().equals("admin") &&
                loginData.getPassword() != null && loginData.getPassword().equals("redhat")) {

            TokenData tokenData = new TokenData();
            tokenData.setLoggedInUserName("Tenant Manager");
            return Uni.createFrom().item(tokenData);
        }
        return Uni.createFrom().failure(() -> new NotAuthorizedException("Invalid credentials"));
    }

}
