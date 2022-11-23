package org.acme.saas.rules.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvisioningResponse {
    private List<ResourcesByTier> resourcesByTier = new ArrayList<>();
}
