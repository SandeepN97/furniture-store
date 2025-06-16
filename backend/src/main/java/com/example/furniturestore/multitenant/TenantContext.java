package com.example.furniturestore.multitenant;

public class TenantContext {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        CURRENT.set(tenantId);
    }

    public static String getTenantId() {
        String tenant = CURRENT.get();
        return tenant == null ? "default" : tenant;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
