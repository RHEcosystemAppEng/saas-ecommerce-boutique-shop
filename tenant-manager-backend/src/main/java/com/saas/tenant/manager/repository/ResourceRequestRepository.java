package com.saas.tenant.manager.repository;

import com.saas.tenant.manager.model.ResourceRequest;
import com.saas.tenant.manager.model.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface ResourceRequestRepository extends CrudRepository<ResourceRequest, Long> {

    @Override
    List<ResourceRequest> findAll();

    List<ResourceRequest> findAllByStatus(String status);

    @Query( nativeQuery = true, value = "SELECT * FROM tenant_db.resource_request where tenant_key=?1 and (status =?2 or status=?3) order by created_date limit 1;")
    ResourceRequest findLatestActiveRequestByTenantKey(String tenantKey, String status1, String status2);

}
