package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.TokenData;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/ops")
public class OpsResource {

    @Operation(summary = "Validates the login credentials for a user with Operation role")
    @APIResponse(responseCode = "200", description = "Login credentials validated", content = @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TokenData.class)))
    @APIResponse(responseCode = "401", description = "Invalid login credentials")
    @POST
    @Path("/login")
    public Uni<TokenData> login(@RequestBody(
            required = true,
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = LoginData.class))) LoginData loginData) {
        if (loginData != null && loginData.getEmail() != null && loginData.getEmail().equals("admin") &&
                loginData.getPassword() != null && loginData.getPassword().equals("redhat")) {

            TokenData tokenData = new TokenData();
            tokenData.setLoggedInUserName("Operations Manager");
            return Uni.createFrom().item(tokenData);
        }
        return Uni.createFrom().failure(() -> new NotAuthorizedException("Invalid credentials"));
    }
}
