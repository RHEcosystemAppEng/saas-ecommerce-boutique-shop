package org.acme.saas.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private long id;
    private String tenantKey;
    private String tenantName;
    private String orgName;
    private String address;
    private String phone;
    private String contactName;
    private String email;
    private String password;
    private String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenantKey")
    private List<Subscription> subscriptions = new ArrayList<>();
}
