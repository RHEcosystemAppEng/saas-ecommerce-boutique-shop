package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@ToString
public class Style extends PanacheEntity {

    public String tenantKey;
    public String headingText;
    public String headingColor;
    public String ribbonColor;

    public static Uni<Style> findByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }
}
