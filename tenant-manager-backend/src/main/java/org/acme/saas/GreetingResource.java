package org.acme.saas;

import io.smallrye.mutiny.Uni;
import org.acme.saas.repository.TenantRepository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject
    TenantRepository tenantRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> hello() {
        return Uni.createFrom().item("Hello from RESTEasy Reactive");
    }



}