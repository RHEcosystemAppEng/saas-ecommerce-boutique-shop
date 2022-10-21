package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.TokenData;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/manager")
public class ManagerResource {

    @Operation(summary = "Validates the login credentials for a user with Manager role")
    @APIResponse(responseCode = "200", description = "Login credentials validated", content = @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TokenData.class)))
    @APIResponse(responseCode = "401", description = "Invalid login credentials")
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> login(@RequestBody(
            required = true,
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = LoginData.class))) LoginData loginData) {

        if (loginData != null && loginData.getEmail() != null && loginData.getEmail().equals("admin") &&
                loginData.getPassword() != null && loginData.getPassword().equals("redhat")) {

            TokenData tokenData = new TokenData();
            tokenData.setLoggedInUserName("Tenant Manager");
            return Uni.createFrom().item(tokenData);
        }
        return Uni.createFrom().failure(() -> new NotAuthorizedException("Invalid credentials"));
    }

}
