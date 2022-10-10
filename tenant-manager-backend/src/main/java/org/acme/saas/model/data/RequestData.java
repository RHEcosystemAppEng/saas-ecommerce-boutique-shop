package org.acme.saas.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestData {

    private String tenantKey;
    private String tier;
    private String hostName;
    private int avgConcurrentShoppers;
    private int peakConcurrentShoppers;
    private String fromTime;
    private String toTime;
}
