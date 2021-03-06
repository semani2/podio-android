package com.podio.sdk.domain;

import java.util.Map;

/**
 * Created by sai on 9/26/16.
 */

public class LinkedAccount {

    public enum Status {
        active,
        revoked
    }

    public enum Provider {
        google,
        legacy_google,
        google_apps,
        citrix,
        sharefile,
        appear_in
    }

    public enum Capability {
        contacts,
        files,
        meetings,
        calendar,
        profile,
        message,
        social,
        addressbook
    }

    public enum Option {
        action_install,
        action_share
    }

    private final Long linked_account_id = null;

    private final Status status = null;

    private final String label = null;

    private final Provider provider = null;

    private final String provider_humanized_name = null;

    private final Map<Capability, String> capability_names = null;

    private final Map<Option, Boolean> options = null;

    public Long getLinkedAccountId() {
        return linked_account_id;
    }

    public Status getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

    public Provider getProvider() {
        return provider;
    }

    public String getProviderHumanizedName() {
        return provider_humanized_name;
    }

    public Map<Capability, String> getCapabilityNames() {
        return capability_names;
    }

    public Map<Option, Boolean> getOptions() {
        return options;
    }
}
