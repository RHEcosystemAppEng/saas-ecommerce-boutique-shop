package com.saas.tenant.manager.service;

import com.saas.tenant.manager.Constants;
import com.saas.tenant.manager.model.ResourceRequest;
import com.saas.tenant.manager.repository.ResourceRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceRequestService {

    private final ResourceRequestRepository resourceRequestRepository;

    public ResourceRequestService(ResourceRequestRepository resourceRequestRepository) {
        this.resourceRequestRepository = resourceRequestRepository;
    }

    public List<ResourceRequest> getAllPendingResourceRequests(){
        return resourceRequestRepository.findAllByStatus(Constants.REQUEST_STATUS_PENDING);
    }

    public ResourceRequest createResourceRequest(ResourceRequest resourceRequest){
        return resourceRequestRepository.save(resourceRequest);
    }

    public ResourceRequest getCurrentRequest(String tenantKey){
        return resourceRequestRepository.findLatestActiveRequestByTenantKey(tenantKey, Constants.REQUEST_STATUS_INITIAL, Constants.REQUEST_STATUS_APPROVED);
    }

    public ResourceRequest getLastValidResourceRequest(String tenantKey) {
        return resourceRequestRepository.findByTenantKeyOrderByUpdatedDateDesc(tenantKey);
    }
}
