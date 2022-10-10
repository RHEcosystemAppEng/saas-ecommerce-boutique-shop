package org.acme.saas.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@ToString
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String tenantKey;
    private String hostName;
    private String tier;
    private int avgConcurrentShoppers;
    private int peakConcurrentShoppers;
    private String fromTime;
    private String toTime;
    private String status;
}
