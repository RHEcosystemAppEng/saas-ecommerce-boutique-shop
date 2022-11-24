package org.acme.saas.rules.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourcesByTier {
    private String tier;
    private List<ResourcesByMicroservice> resourcesByMicroservices = new ArrayList<>();
}