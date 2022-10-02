package com.saas.tenant.manager.util;

import com.saas.tenant.manager.model.ResourceRequest;
import org.springframework.stereotype.Service;

@Service
public class ResourceRequestUtil {

    public ResourceRequest getNewCopy(ResourceRequest input) {
        ResourceRequest output = new ResourceRequest();
        output.setTenantKey(input.getTenantKey());
        output.setTenantName(input.getTenantName());
        output.setTenant(input.getTenant());
        output.setOldMinInstances(input.getOldMinInstances());
        output.setOldMaxInstances(input.getOldMaxInstances());
        output.setNewMinInstances(input.getNewMinInstances());
        output.setNewMaxInstances(input.getNewMaxInstances());
        output.setOldTier(input.getOldTier());
        output.setNewTier(input.getNewTier());
        output.setAvgConcurrentShoppers(input.getAvgConcurrentShoppers());
        output.setPeakConcurrentShoppers(input.getPeakConcurrentShoppers());
        output.setToTime(input.getToTime());
        output.setFromTime(input.getFromTime());
        output.setServiceName(input.getServiceName());
        output.setStatus(input.getStatus());

        return output;
    }
}
