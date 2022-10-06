package com.saas.tenant.manager.repository;

import com.saas.tenant.manager.model.Subscription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    @Override
    List<Subscription> findAll();

    Set<Subscription> findAllByTenantId(long tenantId);

}
