package com.saas.tenant.manager;


import com.google.gson.JsonObject;
import com.saas.tenant.manager.model.ResourceRequest;
import com.saas.tenant.manager.model.Tenant;
import com.saas.tenant.manager.service.ResourceRequestService;
import com.saas.tenant.manager.service.SubscriptionService;
import com.saas.tenant.manager.service.TenantService;
import com.saas.tenant.manager.service.TierService;
import com.saas.tenant.manager.util.ResourceRequestUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@CrossOrigin
public class TenantManager {

    private final TenantService tenantService;

    private final SubscriptionService subscriptionService;

    private final TierService tierService;

    private final ResourceRequestService resourceRequestService;

    private final ResourceRequestUtil resourceRequestUtil;
    public TenantManager(TenantService tenantService, SubscriptionService subscriptionService, TierService tierService, ResourceRequestService resourceRequestService, ResourceRequestUtil resourceRequestUtil) {
        this.tenantService = tenantService;
        this.subscriptionService = subscriptionService;
        this.tierService = tierService;
        this.resourceRequestService = resourceRequestService;
        this.resourceRequestUtil = resourceRequestUtil;
    }

    @GetMapping("/tenant/{id}")
    public ResponseEntity<Tenant> getTenant(@PathVariable String id) {
        Optional<Tenant> optionalTenant = tenantService.getTenantById(Long.valueOf(id));
        return ResponseEntity.ok(optionalTenant.get());
    }

    @GetMapping("/")
    public ResponseEntity<String> getHealthEndpoint() {
        return ResponseEntity.ok("Hello from the app!");
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
            respObj.addProperty("key", optionalUser.get().getTenantKey());
            respObj.addProperty("loggedInUserName", optionalUser.get().getTenantUserName());
            return ResponseEntity.ok(respObj.toString());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/mgr-login")
    public ResponseEntity<String> submitManagerLogin(@RequestBody Map<String, String> reqBody) {
        String email = reqBody.get("email");
        String password = reqBody.get("password");
        if (email.equals("admin") && password.equals("redhat")) {
            JsonObject respObj = new JsonObject();
            respObj.addProperty("loggedInUserName", "Tenant Manager");
            return ResponseEntity.ok(respObj.toString());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/ops-login")
    public ResponseEntity<String> submitOperationLogin(@RequestBody Map<String, String> reqBody) {
        String email = reqBody.get("email");
        String password = reqBody.get("password");
        if (email.equals("admin") && password.equals("redhat")) {
            JsonObject respObj = new JsonObject();
            respObj.addProperty("loggedInUserName", "Tenant Manager");
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
        String hostName = reqBody.get("hostName");
        String avgConcurrentShoppers = reqBody.get("avgConcurrentShoppers");
        String peakConcurrentShoppers = reqBody.get("peakConcurrentShoppers");
        String fromTime = reqBody.get("fromTime");
        String toTime = reqBody.get("toTime");
        Tenant oldTenant = tenantService.save(email, password, tenantName, orgName, address, phone, contactPerson);

        ResourceRequest resourceRequest = new ResourceRequest();
        resourceRequest.setTenant(oldTenant);
        resourceRequest.setTenantName(oldTenant.getTenantUserName());
        resourceRequest.setTenantKey(oldTenant.getTenantKey());
        resourceRequest.setNewTier(serviceLevel);
        resourceRequest.setNewMinInstances(avgConcurrentShoppers);
        resourceRequest.setStatus(Constants.REQUEST_STATUS_INITIAL);
        resourceRequest.setHostName(hostName);
        resourceRequest.setAvgConcurrentShoppers(avgConcurrentShoppers);
        resourceRequest.setPeakConcurrentShoppers(peakConcurrentShoppers);
        resourceRequest.setFromTime(fromTime);
        resourceRequest.setToTime(toTime);

        int[] instanceCount = tierService.calculateInstanceCount(serviceLevel, Integer.parseInt(avgConcurrentShoppers), Integer.parseInt(peakConcurrentShoppers));
        resourceRequest.setNewMinInstances(String.valueOf(instanceCount[0]));
        resourceRequest.setNewMaxInstances(String.valueOf(instanceCount[1]));

        resourceRequestService.createResourceRequest(resourceRequest);
        subscriptionService.save(oldTenant, hostName, serviceLevel);
        Optional<Tenant> latestTenant = tenantService.findLatestTenant(oldTenant);

        JsonObject respObj = new JsonObject();
        respObj.addProperty("id", latestTenant.get().getId());
        respObj.addProperty("key", latestTenant.get().getTenantKey());
        respObj.addProperty("loggedInUserName", latestTenant.get().getTenantUserName());
        return ResponseEntity.ok(respObj.toString());
    }

    @PostMapping(value = "/price-calculation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> priceCalc(@RequestBody Map<String, String> reqBody) {

        String serviceLevel = reqBody.get("serviceLevel");
        String avgConcurrentShoppers = reqBody.get("avgConcurrentShoppers");
        String peakConcurrentShoppers = reqBody.get("peakConcurrentShoppers");
        double price = tierService.calculatePrice(serviceLevel, Integer.parseInt(avgConcurrentShoppers),
                Integer.parseInt(peakConcurrentShoppers), 0);
        return ResponseEntity.ok(String.valueOf(price));
    }

    @PostMapping("/resource-request")
    public ResponseEntity<String> createResourceRequest(@RequestBody Map<String, String> reqBody) {

        String tenantKey = reqBody.get("tenantKey");
        String avgConcurrentShoppers = reqBody.get("avgConcurrentShoppers");
        String peakConcurrentShoppers = reqBody.get("peakConcurrentShoppers");
        String tier = reqBody.get("tier");
        ResourceRequest currentResourceRequest = resourceRequestService.getLastValidResourceRequest(tenantKey);
        ResourceRequest resourceRequest = resourceRequestUtil.getNewCopy(currentResourceRequest);
        resourceRequest.setOldMaxInstances(resourceRequest.getNewMaxInstances());
        resourceRequest.setOldMinInstances(resourceRequest.getNewMinInstances());

        resourceRequest.setAvgConcurrentShoppers(avgConcurrentShoppers);
        resourceRequest.setPeakConcurrentShoppers(peakConcurrentShoppers);
        int[] instanceCount = tierService.calculateInstanceCount(tier,
                Integer.parseInt(avgConcurrentShoppers), Integer.parseInt(peakConcurrentShoppers));
        resourceRequest.setNewMinInstances(String.valueOf(instanceCount[0]));
        resourceRequest.setNewMaxInstances(String.valueOf(instanceCount[1]));
        ResourceRequest savedResourceRequest = resourceRequestService.createResourceRequest(resourceRequest);

        JsonObject respObj = new JsonObject();
        System.out.println("Jude ADDED" + savedResourceRequest.getTenant().getId());
        respObj.addProperty("id", savedResourceRequest.getTenant().getId());
        respObj.addProperty("key", savedResourceRequest.getTenant().getTenantKey());
        respObj.addProperty("loggedInUserName", savedResourceRequest.getTenant().getTenantUserName());
        return ResponseEntity.ok(respObj.toString());
    }

    @GetMapping("/getTierCounts")
    public ResponseEntity<List<?>> getTierCounts() {
        List<?> allTenants = tenantService.getTierCounts();
        System.out.println(allTenants);
        return ResponseEntity.ok(allTenants);
    }

    @GetMapping("/getPendingRequests")
    public ResponseEntity<List<ResourceRequest>> getPendingRequests() {
        List<ResourceRequest> resourceRequestList = resourceRequestService.getAllPendingResourceRequests();
        return ResponseEntity.ok(resourceRequestList);
    }

    @GetMapping("/get-current-request/{tenantKey}")
    public ResponseEntity<ResourceRequest> getCurrentRequest(@PathVariable String tenantKey) {
        ResourceRequest currentRequest = resourceRequestService.getCurrentRequest(tenantKey);
        return ResponseEntity.ok(currentRequest);
    }
}
