package com.saas.tenant.manager;


import com.google.gson.JsonObject;
import com.saas.tenant.manager.model.Tenant;
import com.saas.tenant.manager.service.SubscriptionService;
import com.saas.tenant.manager.service.TenantService;
import com.saas.tenant.manager.service.TierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;

@Controller
@CrossOrigin
public class TenantManager {

    private final TenantService tenantService;

    private final SubscriptionService subscriptionService;

    private final TierService tierService;

    public TenantManager(TenantService tenantService, SubscriptionService subscriptionService, TierService tierService) {
        this.tenantService = tenantService;
        this.subscriptionService = subscriptionService;
        this.tierService = tierService;
    }

    @GetMapping("/tenant/{id}")
    public ResponseEntity<Tenant> getTenant(@PathVariable String id) {
        Optional<Tenant> optionalTenant = tenantService.getTenantById(Long.valueOf(id));
        return ResponseEntity.ok(optionalTenant.get());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<String> checkEmail(@PathVariable String email) {
        boolean emailAlreadyExists = tenantService.isEmailAlreadyExists(email);
        if (emailAlreadyExists) {
            return ResponseEntity.status(HttpStatus.IM_USED).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> submitLogin(@RequestBody Map<String, String> reqBody) {
        String email = reqBody.get("email");
        String password = reqBody.get("password");
        Optional<Tenant> optionalUser = tenantService.findTenant(email, password, true);
        if (optionalUser.isPresent()) {
            JsonObject respObj = new JsonObject();
            respObj.addProperty("id", optionalUser.get().getId());
            respObj.addProperty("loggedInUserName", optionalUser.get().getTenantUserName());
            return ResponseEntity.ok(respObj.toString());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<String> submitSignup(@RequestBody Map<String, String> reqBody) {
        String email = reqBody.get("email");
        String password = reqBody.get("password");
        String tenantName = reqBody.get("tenantName");
        String orgName = reqBody.get("orgName");
        String address = reqBody.get("address");
        String phone = reqBody.get("phone");
        String contactPerson = reqBody.get("contactPerson");
        String serviceLevel = reqBody.get("serviceLevel");
        String avgConcurrentShoppers = reqBody.get("avgConcurrentShoppers");
        String peakConcurrentShoppers = reqBody.get("peakConcurrentShoppers");
        String fromTime = reqBody.get("fromTime");
        String toTime = reqBody.get("toTime");
        Tenant oldTenant = tenantService.save(email, password, tenantName, orgName, address, phone, contactPerson);
        subscriptionService.save(oldTenant, serviceLevel, avgConcurrentShoppers, peakConcurrentShoppers, fromTime, toTime);
        Optional<Tenant> latestTenant = tenantService.findLatestTenant(oldTenant);

        JsonObject respObj = new JsonObject();
        respObj.addProperty("id", latestTenant.get().getId());
        respObj.addProperty("loggedInUserName", latestTenant.get().getTenantUserName());
        return ResponseEntity.ok(respObj.toString());
    }

    @PostMapping("/price-calculation")
    public ResponseEntity<String> priceCalc(@RequestBody Map<String, String> reqBody) {

        String serviceLevel = reqBody.get("serviceLevel");
        String avgConcurrentShoppers = reqBody.get("avgConcurrentShoppers");
        String peakConcurrentShoppers = reqBody.get("peakConcurrentShoppers");
        double price = tierService.calculatePrice(serviceLevel, Integer.parseInt(avgConcurrentShoppers),
                Integer.parseInt(peakConcurrentShoppers), 0);
        return ResponseEntity.ok(String.valueOf(price));
    }
}
