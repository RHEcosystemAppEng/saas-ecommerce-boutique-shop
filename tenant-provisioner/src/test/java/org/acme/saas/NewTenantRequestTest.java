package org.acme.saas;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.acme.saas.provisioner.event.NewTenantRequest;
import org.acme.saas.provisioner.event.ProvisioningRequestStatus;
import org.acme.saas.provisioner.event.ResourceProvisioningStatus;
import org.hamcrest.Matchers;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.google.common.collect.Lists;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
public class NewTenantRequestTest {
    private static final Logger log = Logger.getLogger(NewTenantRequestTest.class);
    private static WireMockServer sink;

    @Inject
    ObjectMapper mapper;

    @BeforeAll
    public static void startSink() {
        sink = new WireMockServer(options().port(8181));
        sink.start();
        sink.stubFor(post("/").willReturn(aResponse().withBody("ok").withStatus(200)));
    }

    @AfterAll
    public static void stopSink() {
        if (sink != null) {
            sink.stop();
        }
    }

    @Test
    public void testNewTenantRequest() throws JsonProcessingException {
        NewTenantRequest newTenantRequest = NewTenantRequest.builder().tenandId(1L).tenantName("AAA").build();

        ProvisioningRequestStatus response = RestAssured.given().contentType("application/json")
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-type", NewTenantRequest.EVENT_TYPE)
                .header("ce-source", "test")
                .body(newTenantRequest)
                .post("/")
                .then().statusCode(200)
                .header("ce-id", notNullValue())
                .header("ce-type", ProvisioningRequestStatus.EVENT_TYPE)
                .header("ce-source", "provisioner")
                .extract().as(ProvisioningRequestStatus.class);

        assertThat(response, is(notNullValue()));
        assertThat(response.getTenantName(), is(newTenantRequest.getTenantName()));
        assertThat(response.getTenandId(), is(newTenantRequest.getTenandId()));
        assertThat(response.getStatus(), is(ProvisioningRequestStatus.Status.Completed));

        sink.verify(6, postRequestedFor(urlEqualTo("/"))
                .withHeader("ce-source", WireMock.equalTo("provisioner")));

        List<ServeEvent> allServeEvents = sink.getAllServeEvents();
        allServeEvents = Lists.reverse(allServeEvents);
        assertThat(allServeEvents, hasSize(6));

        ServeEvent event = allServeEvents.get(0);
        assertThat(event.getRequest().header("ce-type").values().get(0), containsString(ProvisioningRequestStatus.EVENT_TYPE));
        ProvisioningRequestStatus provisioningRequestStatus = mapper.readValue(event.getRequest().getBodyAsString(),
                ProvisioningRequestStatus.class);
        assertThat(provisioningRequestStatus.getStatus(), Matchers.equalTo(ProvisioningRequestStatus.Status.Initiated));

        event = allServeEvents.get(1);
        assertThat(event.getRequest().header("ce-type").values().get(0), containsString(ResourceProvisioningStatus.EVENT_TYPE));
        ResourceProvisioningStatus resourceProvisioningStatus = mapper.readValue(event.getRequest().getBodyAsString(),
                ResourceProvisioningStatus.class);
        assertThat(resourceProvisioningStatus.getStatus(), Matchers.equalTo(ResourceProvisioningStatus.Status.Initiated));
        assertThat(resourceProvisioningStatus.getResourceName(), Matchers.equalTo("AAA"));

        event = allServeEvents.get(2);
        assertThat(event.getRequest().header("ce-type").values().get(0), containsString(ResourceProvisioningStatus.EVENT_TYPE));
        resourceProvisioningStatus = mapper.readValue(event.getRequest().getBodyAsString(), ResourceProvisioningStatus.class);
        assertThat(resourceProvisioningStatus.getStatus(), Matchers.equalTo(ResourceProvisioningStatus.Status.Completed));
        assertThat(resourceProvisioningStatus.getResourceName(), Matchers.equalTo("AAA"));

        event = allServeEvents.get(3);
        assertThat(event.getRequest().header("ce-type").values().get(0), containsString(ResourceProvisioningStatus.EVENT_TYPE));
        resourceProvisioningStatus = mapper.readValue(event.getRequest().getBodyAsString(), ResourceProvisioningStatus.class);
        assertThat(resourceProvisioningStatus.getStatus(), Matchers.equalTo(ResourceProvisioningStatus.Status.Initiated));
        assertThat(resourceProvisioningStatus.getResourceName(), Matchers.equalTo("BBB"));

        event = allServeEvents.get(4);
        assertThat(event.getRequest().header("ce-type").values().get(0), containsString(ResourceProvisioningStatus.EVENT_TYPE));
        resourceProvisioningStatus = mapper.readValue(event.getRequest().getBodyAsString(), ResourceProvisioningStatus.class);
        assertThat(resourceProvisioningStatus.getStatus(), Matchers.equalTo(ResourceProvisioningStatus.Status.Completed));
        assertThat(resourceProvisioningStatus.getResourceName(), Matchers.equalTo("BBB"));

        event = allServeEvents.get(5);
        assertThat(event.getRequest().header("ce-type").values().get(0), containsString(ProvisioningRequestStatus.EVENT_TYPE));
        provisioningRequestStatus = mapper.readValue(event.getRequest().getBodyAsString(),
                ProvisioningRequestStatus.class);
        assertThat(provisioningRequestStatus.getStatus(), Matchers.equalTo(ProvisioningRequestStatus.Status.Completed));
    }
}
