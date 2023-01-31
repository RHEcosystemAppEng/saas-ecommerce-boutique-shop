package org.acme.saas.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterData {

    private String email;
    private String password;
    private String tenantName;
    private String orgName;
    private String address;
    private String phone;
    private String contactName;
    private String tier;
    private String hostName;
    private int avgConcurrentShoppers;
    private int peakConcurrentShoppers;
    private String fromTime;
    private String toTime;
    private String headingText;
    private String headingColor;
    private String ribbonColor;

}
