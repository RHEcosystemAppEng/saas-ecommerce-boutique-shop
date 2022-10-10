package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import org.acme.saas.model.Request;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RequestRepository implements PanacheRepository<Request> {
}
