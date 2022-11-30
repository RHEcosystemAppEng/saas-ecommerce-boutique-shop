package org.acme.saas.common;

public class Constants {
    public static final String TENANT_STATUS_REQUESTED = "Requested";
    public static final String TENANT_STATUS_WAITING = "Waiting";
    public static final String TENANT_STATUS_RUNNING = "Running";
    public static final String TENANT_STATUS_STOPPED = "Stopped";
    public static final String TENANT_STATUS_PURGED = "Purged";

    public static final String REQUEST_STATUS_PENDING = "PENDING";
    public static final String REQUEST_STATUS_APPROVED = "APPROVED";
    public static final String REQUEST_STATUS_REJECTED = "REJECTED";

    public static final String SUBSCRIPTION_STATUS_INITIAL = "INITIAL";
    public static final String SUBSCRIPTION_STATUS_ACTIVE = "ACTIVE";
    public static final String SUBSCRIPTION_STATUS_REJECTED = "REJECTED";

    public static final String REQUEST_SERVICE_NAME_ALL = "ALL";
    public static final String SQL_QUERY_SUBSCRIPTION_SUMMARY = """
                SELECT s.tier, COUNT(*), SUM(r.avgConcurrentShoppers) \
                FROM Subscription s, Request r \
                WHERE s.tenantKey = r.tenantKey AND r.status='APPROVED' \
                GROUP BY s.tier
                """;
}
