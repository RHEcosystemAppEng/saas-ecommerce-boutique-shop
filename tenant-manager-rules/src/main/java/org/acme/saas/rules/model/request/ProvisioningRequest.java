package org.acme.saas.rules.model.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvisioningRequest {
    private List<TierUpdateRequest> tierRequests = new ArrayList<>();
}
