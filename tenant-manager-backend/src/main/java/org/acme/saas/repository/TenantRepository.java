package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import org.acme.saas.model.Tenant;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TenantRepository implements PanacheRepository<Tenant> {

}
