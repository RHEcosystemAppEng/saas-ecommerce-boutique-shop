package com.saas.tenant.manager.service;

import com.saas.tenant.manager.model.Subscription;
import com.saas.tenant.manager.model.Tenant;
import com.saas.tenant.manager.repository.SubscriptionRepository;
import com.saas.tenant.manager.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    private final SubscriptionRepository subscriptionRepository;

    public TenantService(TenantRepository tenantRepository, SubscriptionRepository subscriptionRepository) {
        this.tenantRepository = tenantRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public Tenant save(String email, String pwd, String tenantName, String orgName, String address, String phone, String contactPerson){
        Tenant newTenant = new Tenant();
        newTenant.setTenantUserName(tenantName);
        newTenant.setOrgName(orgName);
        newTenant.setAddress(address);
        newTenant.setPhone(phone);
        newTenant.setContactPerson(contactPerson);
        newTenant.setEmail(email);
        newTenant.setPassword(pwd);
        newTenant.setStatus(true);

        return tenantRepository.save(newTenant);
    }

    public Optional<Tenant> findTenant(String email, String pwd, boolean status) {
        return tenantRepository.findUserByEmailAndPasswordAndStatus(email, pwd, status);
    }

    public Optional<Tenant> findLatestTenant(Tenant oldTenant) {

        Optional<Tenant> byId = tenantRepository.findById(oldTenant.getId());
        Set<Subscription> subscriptions = subscriptionRepository.findAllByTenantId(oldTenant.getId());
        byId.get().setSubscriptionSet(subscriptions);
        return byId;
    }

    public Optional<Tenant> getTenantById(Long tenantId) {
        Optional<Tenant> byId = tenantRepository.findById(tenantId);
//        Set<Subscription> subscriptions = subscriptionRepository.findAllByTenantId(tenantId);
//        byId.get().setSubscriptionSet(subscriptions);
        return byId;
    }

    public boolean isEmailAlreadyExists(String email) {
        Optional<Tenant> optionalTenant = tenantRepository.findByEmail(email);
        return optionalTenant.isPresent();
    }
}
