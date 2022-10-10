package org.acme.saas.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceData {

    private String hostName;
    private String tier;
    private int avgConcurrentShoppers;
    private int peakConcurrentShoppers;
    private String fromTime;
    private String toTime;
}
