package org.acme.saas.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@ToString
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String tenantKey;
    private String serviceName;
    private String tier;
    private int minInstanceCount;
    private int maxInstanceCount;
    private String url;
//    @JsonbTransient
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "tenant_id", foreignKey = @ForeignKey(name = "tenant_id_fk"))
//    private Tenant tenant;
    @OneToOne
    private Request request;
    private String status;
}
