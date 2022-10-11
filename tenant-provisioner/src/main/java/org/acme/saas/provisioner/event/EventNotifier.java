package org.acme.saas.provisioner.event;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;

@Path("/")
@RegisterRestClient
public interface EventNotifier {

    // This will emit binary encoded events.
    // To use structured JSON encoding use @Produces(JsonFormat.CONTENT_TYPE).
    @POST
    @Produces(JsonFormat.CONTENT_TYPE)
    void emit(CloudEvent event);
}
