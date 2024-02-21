package com.homihq.db2rest.auth.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyKeyResponse {

    public String code;

    public boolean enabled;

    public int expires;

    public boolean valid;

    public String keyId;

    public Meta meta;

    public String name;

    public String ownerId;

    public String[] permissions;

    public RateLimit rateLimit;

    public int remaining;

    static class Meta {
        String[] roles;
        String stripeCustomerId;
    }

    static class RateLimit {
        int limit;
        int remaining;
        int reset;
    }
}
