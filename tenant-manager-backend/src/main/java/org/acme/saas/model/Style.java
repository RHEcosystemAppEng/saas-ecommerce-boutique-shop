package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@ToString
public class Style extends PanacheEntity {

    public String tenantKey;
    public String headingText;
    public String headingColor;
    public String ribbonColor;

}
