package com.saas.tenant.manager.repository;

import com.saas.tenant.manager.model.Tenant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TenantRepository extends CrudRepository<Tenant, Long> {

    @Override
    List<Tenant> findAll();

    @Transactional
    Optional<Tenant> findUserByEmailAndPasswordAndStatus(String email, String pwd, boolean status);

    Optional<Tenant> findById(Long id);

    Optional<Tenant> findByEmail(String email);

    @Query( nativeQuery = true, value = "SELECT s.service_level, COUNT(*) FROM tenant_db.tenant t, tenant_db.subscription s WHERE t.id = s.tenant GROUP BY s.service_level")
    List<?> getTierCount();

}
