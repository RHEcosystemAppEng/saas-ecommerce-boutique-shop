package com.saas.tenant.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Getter
@Setter
@Entity
public class ResourceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String tenantKey;
    @Column(name = "tenant_name")
    private String tenantName;
    @Column(name = "service_name")
    private String serviceName;
    @Column(name = "old_min_instances")
    private String oldMinInstances;
    @Column(name = "old_max_instances")
    private String oldMaxInstances;
    @Column(name = "new_min_instances")
    private String newMinInstances;
    @Column(name = "new_max_instances")
    private String newMaxInstances;
    @Column(name = "old_tier")
    private String oldTier;
    @Column(name = "new_tier")
    private String newTier;
    @Column(name = "requesting_tier")
    private String requestingTier;
    private String avgConcurrentShoppers;
    private String peakConcurrentShoppers;
    private String fromTime;
    private String toTime;
    private String status;

    @CreatedDate
    private Instant createdDate;

    @UpdateTimestamp
    private Instant updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant", nullable=false)
    @JsonIgnore
    private Tenant tenant;

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
