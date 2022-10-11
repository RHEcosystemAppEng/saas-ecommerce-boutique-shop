package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.TokenData;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/ops")
public class OpsResource {

    @POST
    @Path("/login")
    public Uni<TokenData> login(LoginData loginData) {
        if (loginData != null && loginData.getEmail() != null && loginData.getEmail().equals("admin") &&
                loginData.getPassword() != null && loginData.getPassword().equals("redhat")) {

            TokenData tokenData = new TokenData();
            tokenData.setLoggedInUserName("Operations Manager");
            return Uni.createFrom().item(tokenData);
        }
        return Uni.createFrom().failure(() -> new NotAuthorizedException("Invalid credentials"));
    }
}
