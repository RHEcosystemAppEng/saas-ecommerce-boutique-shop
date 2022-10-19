package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.ToString;
import org.acme.saas.common.Constants;

import javax.persistence.Entity;
import java.util.List;

@Entity
@ToString
public class Request extends PanacheEntity {

    public String tenantKey;
    public String hostName;
    public String serviceName;
    public String tier;
    public int avgConcurrentShoppers;
    public int peakConcurrentShoppers;
    public String fromTime;
    public String toTime;
    public String status;

    public static Uni<List<Request>> findAllByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).list();
    }

    public static Uni<List<Request>> findAllPendingRequests() {
        return find("status=?1", Constants.REQUEST_STATUS_PENDING).list();
    }
}
