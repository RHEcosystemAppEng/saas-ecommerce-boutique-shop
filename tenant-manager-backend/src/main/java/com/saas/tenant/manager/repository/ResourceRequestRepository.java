package com.saas.tenant.manager.repository;

import com.saas.tenant.manager.model.ResourceRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResourceRequestRepository extends CrudRepository<ResourceRequest, Long> {

    @Override
    List<ResourceRequest> findAll();

    List<ResourceRequest> findAllByStatus(String status);

    @Query( nativeQuery = true, value = "SELECT * FROM tenant_db.resource_request where tenant_key=?1 and (status =?2 or status=?3) order by created_date desc limit 1;")
    ResourceRequest findLatestActiveRequestByTenantKey(String tenantKey, String status1, String status2);

    @Query( nativeQuery = true, value = "SELECT * FROM tenant_db.resource_request r WHERE r.tenant_key = ?1 AND r.status in (\"Initial\", \"Approved\") ORDER BY r.updated_date desc LIMIT 1;")
    ResourceRequest findByTenantKeyOrderByUpdatedDateDesc(String tenantKey);
}
